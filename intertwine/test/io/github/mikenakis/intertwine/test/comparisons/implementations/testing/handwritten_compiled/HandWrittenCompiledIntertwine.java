package io.github.mikenakis.intertwine.test.comparisons.implementations.testing.handwritten_compiled;

import io.github.mikenakis.bytecode.ByteCodeClassLoader;
import io.github.mikenakis.bytecode.model.ByteCodeField;
import io.github.mikenakis.bytecode.model.ByteCodeMethod;
import io.github.mikenakis.bytecode.model.ByteCodeType;
import io.github.mikenakis.bytecode.model.attributes.CodeAttribute;
import io.github.mikenakis.bytecode.model.attributes.StackMapTableAttribute;
import io.github.mikenakis.bytecode.model.attributes.code.Instruction;
import io.github.mikenakis.bytecode.model.attributes.code.instructions.TableSwitchInstruction;
import io.github.mikenakis.bytecode.model.attributes.stackmap.verification.ObjectVerificationType;
import io.github.mikenakis.bytecode.model.descriptors.FieldPrototype;
import io.github.mikenakis.bytecode.model.descriptors.FieldReference;
import io.github.mikenakis.bytecode.model.descriptors.MethodPrototype;
import io.github.mikenakis.bytecode.model.descriptors.MethodReference;
import io.github.mikenakis.bytecode.model.descriptors.MethodReferenceKind;
import io.github.mikenakis.intertwine.Anycall;
import io.github.mikenakis.intertwine.Intertwine;
import io.github.mikenakis.intertwine.MethodKey;
import io.github.mikenakis.intertwine.test.comparisons.rig.Alpha;
import io.github.mikenakis.intertwine.test.comparisons.rig.FooInterface;
import io.github.mikenakis.java_type_model.FieldDescriptor;
import io.github.mikenakis.java_type_model.MethodDescriptor;
import io.github.mikenakis.java_type_model.TerminalTypeDescriptor;
import io.github.mikenakis.java_type_model.TypeDescriptor;
import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.kit.logging.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Compiling {@link Intertwine}.
 *
 * @author michael.gr
 */
class HandWrittenCompiledIntertwine implements Intertwine<FooInterface>
{
	private final HandWrittenCompiledKey[] keys;
	private final Map<MethodPrototype,HandWrittenCompiledKey> keysByPrototype;
	private final Map<Method,HandWrittenCompiledKey> keysByMethod;
	private static Optional<Class<FooInterface>> cachedEntwinerClass = Optional.empty(); //we have to cache the entwiner class because we create it, and a class cannot be redefined.
	private static Optional<Class<Anycall<FooInterface>>> cachedUntwinerClass = Optional.empty(); //we have to cache the untwiner class because we create it, and a class cannot be redefined.
	private static final TypeDescriptor alphaTypeDescriptor = TypeDescriptor.of( Alpha.class.getName() );

	HandWrittenCompiledIntertwine()
	{
		ByteCodeType interfaceByteCodeType = ByteCodeType.read( FooInterface.class );
		List<ByteCodeMethod> byteCodeMethods = interfaceByteCodeType.methods;
		keys = IntStream.range( 0, byteCodeMethods.size() ).mapToObj( i -> createKey( i, byteCodeMethods.get( i ), FooInterface.class ) ).toArray( HandWrittenCompiledKey[]::new );
		keysByPrototype = Stream.of( keys ).collect( Collectors.toMap( k -> k.methodPrototype, k -> k ) );
		keysByMethod = Stream.of( keys ).collect( Collectors.toMap( k -> k.method, k -> k ) );
	}

	private HandWrittenCompiledKey createKey( int methodIndex, ByteCodeMethod byteCodeMethod, Class<?> interfaceClass )
	{
		MethodPrototype methodPrototype = byteCodeMethod.prototype();//.of( method );
		Class<?>[] parameterTypes = methodPrototype.descriptor.parameterTypeDescriptors.stream().<Class<?>>map( d -> classFromTypeDescriptor( d ) ).toArray( Class<?>[]::new );
		Method reflectionMethod = Kit.unchecked( () -> interfaceClass.getMethod( methodPrototype.methodName, parameterTypes ) );
		return new HandWrittenCompiledKey( this, reflectionMethod, methodPrototype, methodIndex );
	}

	private static Class<?> classFromTypeDescriptor( TypeDescriptor typeDescriptor )
	{
		if( typeDescriptor.isPrimitive() )
			return typeDescriptor.asPrimitiveTypeDescriptor().jvmClass;
		String typeName = typeDescriptor.typeName();
		return Kit.unchecked( () -> Class.forName( typeName ) );
	}

	@Override public Class<FooInterface> interfaceType()
	{
		return FooInterface.class;
	}

	@Override public List<MethodKey<FooInterface>> keys()
	{
		return List.of( keys );
	}

	@Override public MethodKey<FooInterface> keyByMethodPrototype( MethodPrototype methodPrototype )
	{
		return Kit.map.get( keysByPrototype, methodPrototype );
	}

	/*
	 NOTE: The generated class, as well as its constructor, must both be public. Package-private will not work, even if we make the package of the generated
	       class be the same as our package, because the class-loader of the generated class will be different from our class-loader, so java access rules will
	       deny package-private access regardless of the package.
	 */

	/*
		mikenakis.classdump.ClassDumpMain.classDump(ClassDumpMain.java:152) | DEBUG | 1 | 06:32:29.469 | main | Dumping C:\Users\MBV\Out\mikenakis\intertwine\test-classes\mikenakis\test\intertwine\handwritten\HandwrittenEntwiner.class (Optional[C:\Users\MBV\Personal\IdeaProjects\mikenakis-personal3\Public\intertwine\test\mikenakis\test\intertwine\handwritten])
		■ ByteCodeType version = 60.0, accessFlags = [final, super], this = mikenakis.test.intertwine.handwritten.HandwrittenEntwiner, super = java.lang.Object
		├─■ interfaces: 1 items
		│ └─■ [0] mikenakis.test.intertwine.comparisons.rig.FooInterface
		├─■ extraTypes: 0 items
		├─■ fields: 2 items
		│ ├─■ [0] ByteCodeField accessFlags = [private, final], prototype = mikenakis.test.intertwine.handwritten.HandwrittenKey[] keys
		│ │ └─■ attributeSet: 0 items
		│ └─■ [1] ByteCodeField accessFlags = [private, final], prototype = mikenakis.intertwine.Anycall exitPoint
		│   └─■ attributeSet: 0 items
		├─■ methods: 4 items
	*/
	@Override public FooInterface newEntwiner( Anycall<FooInterface> exitPoint )
	{
		Class<FooInterface> entwinerClass = cachedEntwinerClass.orElseGet( () -> {
			Class<FooInterface> c = createEntwinerClass();
			cachedEntwinerClass = Optional.of( c );
			return c;
		} );
		Constructor<FooInterface> constructor = Kit.unchecked( () -> entwinerClass.getDeclaredConstructor( HandWrittenCompiledKey[].class, Anycall.class ) );
		return Kit.unchecked( () -> constructor.newInstance( keys, exitPoint ) );
	}

	private Class<FooInterface> createEntwinerClass()
	{
		TypeDescriptor arrayOfKeyTypeDescriptor = TypeDescriptor.of( HandWrittenCompiledKey[].class );
		TypeDescriptor anycallTypeDescriptor = TypeDescriptor.of( Anycall.class );
		ByteCodeType byteCodeType = ByteCodeType.of( //
			ByteCodeType.modifierEnum.of( ByteCodeType.Modifier.Public, ByteCodeType.Modifier.Final, ByteCodeType.Modifier.Super ), //
			TerminalTypeDescriptor.of( "HandwrittenCompiledEntwiner_" + identifierFromTypeName( FooInterface.class ) ), //
			Optional.of( TerminalTypeDescriptor.of( Object.class ) ), //
			List.of( TerminalTypeDescriptor.of( FooInterface.class ) ) );
		ByteCodeField keysField = ByteCodeField.of( ByteCodeField.modifierEnum.of( ByteCodeField.Modifier.Private, ByteCodeField.Modifier.Final ), //
			FieldPrototype.of( "keys", FieldDescriptor.of( arrayOfKeyTypeDescriptor ) ) );
		byteCodeType.fields.add( keysField );
		ByteCodeField exitPointField = ByteCodeField.of( ByteCodeField.modifierEnum.of( ByteCodeField.Modifier.Private, ByteCodeField.Modifier.Final ), //
			FieldPrototype.of( "exitPoint", FieldDescriptor.of( anycallTypeDescriptor ) ) );
		byteCodeType.fields.add( exitPointField );
		addEntwinerInitMethod( byteCodeType, keysField, exitPointField );

		for( Method method : FooInterface.class.getMethods() )
		{
			int modifiers = method.getModifiers();
			if( Modifier.isStatic( modifiers ) )
				continue;
			if( !Modifier.isAbstract( modifiers ) )
				continue;
			assert Modifier.isPublic( modifiers );
			assert !Modifier.isFinal( modifiers );
			assert !Modifier.isNative( modifiers );
			assert !Modifier.isSynchronized( modifiers ); // ?
			assert !method.isSynthetic(); // ?
			assert !method.isDefault(); // ?
			assert !method.isBridge(); // ?
			if( Kit.get( false ) )
				addEntwinerInterfaceMethod0( byteCodeType, keysField, exitPointField, 0, MethodPrototype.of( method ) );
		}
		addEntwinerInterfaceMethod0( byteCodeType, keysField, exitPointField, 0, MethodPrototype.of( "voidMethod", MethodDescriptor.of( TypeDescriptor.of( void.class ), List.of() ) ) );
		addEntwinerInterfaceMethod1( byteCodeType, keysField, exitPointField, 1, MethodPrototype.of( "getAlpha", MethodDescriptor.of( alphaTypeDescriptor, TypeDescriptor.of( int.class ) ) ) );
		addEntwinerInterfaceMethod2( byteCodeType, keysField, exitPointField, 2, MethodPrototype.of( "setAlpha", MethodDescriptor.of( TypeDescriptor.of( void.class ), TypeDescriptor.of( int.class ), alphaTypeDescriptor ) ) );

//		saveBytes( Path.of( "C:\\temp\\intertwine", byteCodeType.typeDescriptor().typeName + ".class" ), byteCodeType.write() );
//		System.out.println( ByteCodePrinter.printByteCodeType( byteCodeType, Optional.empty() ) );

		return ByteCodeClassLoader.load( getClass().getClassLoader(), byteCodeType );
	}

	/*
		│ ├─■ [0] ByteCodeMethod accessFlags = [], prototype = void <init>( mikenakis.test.intertwine.handwritten.HandwrittenKey[], mikenakis.intertwine.Anycall )
		│ │ └─■ attributes: 1 items
		│ │   └─■ [0] CodeAttribute maxStack = 2, maxLocals = 3
		│ │     ├─■ instructions: 9 entries
		│ │     │ ├─■ L14: // {
		│ │     │ ├─■         ALOAD 0
		│ │     │ ├─■         INVOKESPECIAL reference = Plain void java.lang.Object.<init>()
		│ │     │ ├─■ L15: // this.keys = keys;
		│ │     │ ├─■         ALOAD 0
		│ │     │ ├─■         ALOAD 1
		│ │     │ ├─■         PUTFIELD mikenakis.test.intertwine.handwritten.HandwrittenKey[] mikenakis.test.intertwine.handwritten.HandwrittenEntwiner.keys
		│ │     │ ├─■ L16: // this.exitPoint = exitPoint;
		│ │     │ ├─■         ALOAD 0
		│ │     │ ├─■         ALOAD 2
		│ │     │ ├─■         PUTFIELD mikenakis.intertwine.Anycall mikenakis.test.intertwine.handwritten.HandwrittenEntwiner.exitPoint
		│ │     │ ├─■ L17: // }
		│ │     │ └─■         RETURN
		│ │     ├─■ exceptionInfos: 0 items
		│ │     └─■ attributeSet: 0 items
	*/
	private static void addEntwinerInitMethod( ByteCodeType byteCodeType, ByteCodeField keysField, ByteCodeField exitPointField )
	{
		ByteCodeMethod method = ByteCodeMethod.of( //
			ByteCodeMethod.modifierEnum.of( ByteCodeMethod.Modifier.Public ), //
			MethodPrototype.of( "<init>", //
				MethodDescriptor.of( TypeDescriptor.of( void.class ), keysField.descriptor().typeDescriptor, exitPointField.descriptor().typeDescriptor ) ) );
		byteCodeType.methods.add( method );
		CodeAttribute code = CodeAttribute.of( 2, 3 );
		method.attributeSet.addAttribute( code );
		code.ALOAD( 0 );
		code.INVOKESPECIAL( MethodReference.of( MethodReferenceKind.Plain, Object.class, //
			MethodPrototype.of( "<init>", MethodDescriptor.of( void.class ) ) ) );
		code.ALOAD( 0 );
		code.ALOAD( 1 );
		code.PUTFIELD( FieldReference.of( byteCodeType.typeDescriptor(), keysField.prototype() ) );
		code.ALOAD( 0 );
		code.ALOAD( 2 );
		code.PUTFIELD( FieldReference.of( byteCodeType.typeDescriptor(), exitPointField.prototype() ) );
		code.RETURN();
	}

	/*
		│ ├─■ [1] ByteCodeMethod accessFlags = [public], prototype = void voidMethod()
		│ │ └─■ attributes: 1 items
		│ │   └─■ [0] CodeAttribute maxStack = 3, maxLocals = 1
		│ │     ├─■ instructions: 10 entries
		│ │     │ ├─■ L21: // exitPoint.anycall( keys[0], Kit.ARRAY_OF_ZERO_OBJECTS );
		│ │     │ ├─■         ALOAD 0
		│ │     │ ├─■         GETFIELD mikenakis.intertwine.Anycall mikenakis.test.intertwine.handwritten.HandwrittenEntwiner.exitPoint
		│ │     │ ├─■         ALOAD 0
		│ │     │ ├─■         GETFIELD mikenakis.test.intertwine.handwritten.HandwrittenKey[] mikenakis.test.intertwine.handwritten.HandwrittenEntwiner.keys
		│ │     │ ├─■         LDC 0
		│ │     │ ├─■         AALOAD
		│ │     │ ├─■         GETSTATIC java.lang.Object[] mikenakis.kit.Kit.ARRAY_OF_ZERO_OBJECTS
		│ │     │ ├─■         INVOKEINTERFACE reference = Interface java.lang.Object mikenakis.intertwine.Anycall.anycall( mikenakis.intertwine.MethodKey, java.lang.Object[] ), 3 arguments
		│ │     │ ├─■         POP
		│ │     │ ├─■ L22: // }
		│ │     │ └─■         RETURN
		│ │     ├─■ exceptionInfos: 0 items
		│ │     └─■ attributeSet: 0 items
	*/
	private static void addEntwinerInterfaceMethod0( ByteCodeType byteCodeType, ByteCodeField keysField, ByteCodeField exitPointField, int methodIndex, //
		MethodPrototype methodPrototype )
	{
		assert methodIndex == 0;
		assert methodPrototype.descriptor.parameterTypeDescriptors.isEmpty();
		ByteCodeMethod method = ByteCodeMethod.of( ByteCodeMethod.modifierEnum.of( ByteCodeMethod.Modifier.Public ), methodPrototype );
		byteCodeType.methods.add( method );
		CodeAttribute code = CodeAttribute.of( 3, 1 );
		method.attributeSet.addAttribute( code );
		code.ALOAD( 0 ); //push this
		code.GETFIELD( FieldReference.of( byteCodeType.typeDescriptor(), exitPointField.prototype() ) ); //pop this, push this->exitPoint
		code.ALOAD( 0 ); //push this
		code.GETFIELD( FieldReference.of( byteCodeType.typeDescriptor(), keysField.prototype() ) ); //pop this, push keys
		code.LDC( methodIndex ); //push methodIndex
		code.AALOAD(); //pop methodIndex, pop keys, push keys[methodIndex]
		code.LDC( methodPrototype.parameterCount() ); //push method-argument-count
		code.ANEWARRAY( TerminalTypeDescriptor.of( Object.class ) ); //pop method-argument-count, push new Object[method-argument-count]

		code.INVOKEINTERFACE( Anycall.methodReference(), 3 );
		code.POP(); //discard result
		code.RETURN(); //just return.
	}

	/*
		│ ├─■ [2] ByteCodeMethod accessFlags = [public], prototype = mikenakis.test.intertwine.comparisons.rig.Alpha getAlpha( int )
		│ │ └─■ attributes: 1 items
		│ │   └─■ [0] CodeAttribute maxStack = 6, maxLocals = 2
		│ │     ├─■ instructions: 16 entries
		│ │     │ ├─■ L26: // return (Alpha)exitPoint.anycall( keys[1], new Object[] { index } );
		│ │     │ ├─■         ALOAD 0
		│ │     │ ├─■         GETFIELD mikenakis.intertwine.Anycall mikenakis.test.intertwine.handwritten.HandwrittenEntwiner.exitPoint
		│ │     │ ├─■         ALOAD 0
		│ │     │ ├─■         GETFIELD mikenakis.test.intertwine.handwritten.HandwrittenKey[] mikenakis.test.intertwine.handwritten.HandwrittenEntwiner.keys
		│ │     │ ├─■         LDC 1
		│ │     │ ├─■         AALOAD
		│ │     │ ├─■         LDC 1
		│ │     │ ├─■         ANEWARRAY java.lang.Object
		│ │     │ ├─■         DUP
		│ │     │ ├─■         LDC 0
		│ │     │ ├─■         ILOAD 1
		│ │     │ ├─■         INVOKESTATIC reference = Plain java.lang.Integer java.lang.Integer.valueOf( int )
		│ │     │ ├─■         AASTORE
		│ │     │ ├─■         INVOKEINTERFACE reference = Interface java.lang.Object mikenakis.intertwine.Anycall.anycall( mikenakis.intertwine.MethodKey, java.lang.Object[] ), 3 arguments
		│ │     │ ├─■         CHECKCAST mikenakis.test.intertwine.comparisons.rig.Alpha
		│ │     │ └─■         ARETURN
		│ │     ├─■ exceptionInfos: 0 items
		│ │     └─■ attributeSet: 0 items
	*/
	private static void addEntwinerInterfaceMethod1( ByteCodeType byteCodeType, ByteCodeField keysField, ByteCodeField exitPointField, int methodIndex, MethodPrototype methodPrototype )
	{
		assert methodIndex == 1;
		assert methodPrototype.parameterCount() == 1;
		assert methodPrototype.descriptor.parameterTypeDescriptors.get( 0 ).equals( TypeDescriptor.of( int.class ) );
		ByteCodeMethod method = ByteCodeMethod.of( ByteCodeMethod.modifierEnum.of( ByteCodeMethod.Modifier.Public ), methodPrototype );
		byteCodeType.methods.add( method );
		CodeAttribute code = CodeAttribute.of( 6, 2 );
		method.attributeSet.addAttribute( code );

		code.ALOAD( 0 ); //push this
		code.GETFIELD( FieldReference.of( byteCodeType.typeDescriptor(), exitPointField.prototype() ) ); //pop this, push this->exitPoint

		code.ALOAD( 0 ); //push this
		code.GETFIELD( FieldReference.of( byteCodeType.typeDescriptor(), keysField.prototype() ) ); //pop this, push this->keys
		code.LDC( methodIndex ); //push methodIndex
		code.AALOAD(); //pop methodIndex, pop this->keys, push this->keys[methodIndex]

		code.LDC( methodPrototype.parameterCount() ); //push method-argument-count
		code.ANEWARRAY( TerminalTypeDescriptor.of( Object.class ) ); //pop method-argument-count, push new Object[method-argument-count]

		for( int argumentIndex = 0; argumentIndex < methodPrototype.parameterCount(); argumentIndex++ )
		{
			code.DUP(); //pop Object[], push Object[], Object[]
			code.LDC( argumentIndex ); //push argumentIndex
			if( methodPrototype.descriptor.parameterTypeDescriptors.get( 0 ).isPrimitive() )
			{
				code.ILOAD( 1 + argumentIndex ); //push arguments[argumentIndex]
				if( methodPrototype.descriptor.parameterTypeDescriptors.get( 0 ).isPrimitive() )
					code.INVOKESTATIC( MethodReference.of( MethodReferenceKind.Plain, Integer.class, //pop arguments[1], push boxed(arguments[1])
						MethodPrototype.of( "valueOf", MethodDescriptor.of( Integer.class, int.class ) ) ) );
			}
			else
			{
				assert false;
			}
			code.AASTORE(); //pop boxed(arguments[1]), pop argumentIndex, pop Object[], set Object[argumentIndex] = boxed(arguments[1])
		}

		code.INVOKEINTERFACE( Anycall.methodReference(), 3 );
		code.CHECKCAST( alphaTypeDescriptor ); //check type of result
		code.ARETURN(); //pop result, return it.
	}

	/*
		│ └─■ [3] ByteCodeMethod accessFlags = [public], prototype = void setAlpha( int, mikenakis.test.intertwine.comparisons.rig.Alpha )
		│   └─■ attributes: 1 items
		│     └─■ [0] CodeAttribute maxStack = 6, maxLocals = 3
		│       ├─■ instructions: 20 entries
		│       │ ├─■ L31: // exitPoint.anycall( keys[2], new Object[] { index, alpha } );
		│       │ ├─■         ALOAD 0
		│       │ ├─■         GETFIELD mikenakis.intertwine.Anycall mikenakis.test.intertwine.handwritten.HandwrittenEntwiner.exitPoint
		│       │ ├─■         ALOAD 0
		│       │ ├─■         GETFIELD mikenakis.test.intertwine.handwritten.HandwrittenKey[] mikenakis.test.intertwine.handwritten.HandwrittenEntwiner.keys
		│       │ ├─■         LDC 2
		│       │ ├─■         AALOAD
		│       │ ├─■         LDC 2
		│       │ ├─■         ANEWARRAY java.lang.Object
		│       │ ├─■         DUP
		│       │ ├─■         LDC 0
		│       │ ├─■         ILOAD 1
		│       │ ├─■         INVOKESTATIC reference = Plain java.lang.Integer java.lang.Integer.valueOf( int )
		│       │ ├─■         AASTORE
		│       │ ├─■         DUP
		│       │ ├─■         LDC 1
		│       │ ├─■         ALOAD 2
		│       │ ├─■         AASTORE
		│       │ ├─■         INVOKEINTERFACE reference = Interface java.lang.Object mikenakis.intertwine.Anycall.anycall( mikenakis.intertwine.MethodKey, java.lang.Object[] ), 3 arguments
		│       │ ├─■         POP
		│       │ ├─■ L32: // }
		│       │ └─■         RETURN
		│       ├─■ exceptionInfos: 0 items
		│       └─■ attributeSet: 0 items
		└─■ attributeSet: 0 items
	*/
	private static void addEntwinerInterfaceMethod2( ByteCodeType byteCodeType, ByteCodeField keysField, ByteCodeField exitPointField, int methodIndex, MethodPrototype methodPrototype )
	{
		assert methodIndex == 2;
		assert methodPrototype.parameterCount() == 2;
		ByteCodeMethod method = ByteCodeMethod.of( ByteCodeMethod.modifierEnum.of( ByteCodeMethod.Modifier.Public ), methodPrototype );
		byteCodeType.methods.add( method );
		CodeAttribute code = CodeAttribute.of( 6, 3 );
		method.attributeSet.addAttribute( code );

		code.ALOAD( 0 ); //push this
		code.GETFIELD( FieldReference.of( byteCodeType.typeDescriptor(), exitPointField.prototype() ) ); //pop this, push this->exitPoint

		code.ALOAD( 0 ); //push this
		code.GETFIELD( FieldReference.of( byteCodeType.typeDescriptor(), keysField.prototype() ) ); //pop this, push keys
		code.LDC( methodIndex ); //push methodIndex
		code.AALOAD(); //pop methodIndex, pop keys, push keys[methodIndex]

		code.LDC( methodPrototype.parameterCount() ); //push method-argument-count
		code.ANEWARRAY( TerminalTypeDescriptor.of( Object.class ) ); //pop method-argument-count, push new Object[method-argument-count]

		emitValueOf( code, 0 );

		emitStore( code, 1 );

		code.INVOKEINTERFACE( Anycall.methodReference(), 3 );
		code.POP();
		code.RETURN();
	}

	private static void emitValueOf( CodeAttribute code, int argumentIndex )
	{
		code.DUP();
		code.LDC( argumentIndex );
		code.ILOAD( 1 + argumentIndex );
		code.INVOKESTATIC( MethodReference.of( MethodReferenceKind.Plain, Integer.class, MethodPrototype.of( "valueOf", MethodDescriptor.of( Integer.class, int.class ) ) ) );
		code.AASTORE();
	}

	private static void emitStore( CodeAttribute code, int argumentIndex )
	{
		code.DUP();
		code.LDC( argumentIndex );
		code.ALOAD( 1 + argumentIndex );
		code.AASTORE();
	}

	@Override public Anycall<FooInterface> newUntwiner( FooInterface exitPoint )
	{
		Class<Anycall<FooInterface>> untwinerClass = cachedUntwinerClass.orElseGet( () -> {
			Class<Anycall<FooInterface>> c = createUntwinerClass();
			cachedUntwinerClass = Optional.of( c );
			return c;
		} );
		return Kit.unchecked( () -> //
		{
			Constructor<Anycall<FooInterface>> constructor = untwinerClass.getDeclaredConstructor( FooInterface.class );
			return constructor.newInstance( exitPoint );
		} );
	}

	/*
		mikenakis.classdump.ClassDumpMain.classDump(ClassDumpMain.java:150) | DEBUG | 1 | 16:25:44.815 | main | Dumping C:\Users\MBV\Out\mikenakis\intertwine\test-classes\mikenakis\test\intertwine\handwritten\HandwrittenUntwiner.class
		■ ByteCodeType version = 60.0, accessFlags = [super], this = mikenakis.test.intertwine.handwritten.HandwrittenUntwiner, super = java.lang.Object
		├─■ interfaces: 1 items
		│ └─■ [0] mikenakis.intertwine.Anycall
		├─■ extraTypes: 0 items
		├─■ fields: 1 items
		│ └─■ [0] ByteCodeField accessFlags = [private, final], prototype = mikenakis.test.intertwine.comparisons.rig.FooInterface exitPoint
		│   └─■ attributeSet: 0 items
		├─■ methods: 2 items
	*/
	private Class<Anycall<FooInterface>> createUntwinerClass()
	{
		TypeDescriptor fooInterfaceTypeDescriptor = TypeDescriptor.of( FooInterface.class );
		ByteCodeType byteCodeType = ByteCodeType.of( //
			ByteCodeType.modifierEnum.of( ByteCodeType.Modifier.Public, ByteCodeType.Modifier.Final, ByteCodeType.Modifier.Super ), //
			TerminalTypeDescriptor.of( "HandwrittenCompiledUntwiner_" + identifierFromTypeName( FooInterface.class ) ), //
			Optional.of( TerminalTypeDescriptor.of( Object.class ) ), //
			List.of( TerminalTypeDescriptor.of( Anycall.class ) ) );
		ByteCodeField exitPointField = ByteCodeField.of( ByteCodeField.modifierEnum.of( ByteCodeField.Modifier.Private, ByteCodeField.Modifier.Final ), //
			FieldPrototype.of( "exitPoint", FieldDescriptor.of( fooInterfaceTypeDescriptor ) ) );
		byteCodeType.fields.add( exitPointField );
		addUntwinerInitMethod( byteCodeType, exitPointField );

		addUntwinerAnycallMethod( byteCodeType, exitPointField );

//		saveBytes( Path.of( "C:\\temp\\intertwine", byteCodeType.typeDescriptor().typeName + ".class" ), byteCodeType.write() );
//		System.out.println( ByteCodePrinter.printByteCodeType( byteCodeType, Optional.empty() ) );

		return ByteCodeClassLoader.load( getClass().getClassLoader(), byteCodeType );
	}

	/*
		│ ├─■ [0] ByteCodeMethod accessFlags = [], prototype = void <init>( mikenakis.test.intertwine.comparisons.rig.FooInterface )
		│ │ └─■ attributes: 1 items
		│ │   └─■ [0] CodeAttribute maxStack = 2, maxLocals = 2
		│ │     ├─■ instructions: 6 entries
		│ │     │ ├─■ L13:    ALOAD 0
		│ │     │ ├─■         INVOKESPECIAL reference = Plain void java.lang.Object.<init>()
		│ │     │ ├─■ L14:    ALOAD 0
		│ │     │ ├─■         ALOAD 1
		│ │     │ ├─■         PUTFIELD mikenakis.test.intertwine.comparisons.rig.FooInterface mikenakis.test.intertwine.handwritten.HandwrittenUntwiner.exitPoint
		│ │     │ └─■ L15:    RETURN
		│ │     ├─■ exceptionInfos: 0 items
		│ │     └─■ attributeSet: 0 items
	*/
	private static void addUntwinerInitMethod( ByteCodeType byteCodeType, ByteCodeField exitPointField )
	{
		ByteCodeMethod byteCodeMethod = ByteCodeMethod.of( ByteCodeMethod.modifierEnum.of( ByteCodeMethod.Modifier.Public ), //
			MethodPrototype.of( "<init>", MethodDescriptor.of( void.class, FooInterface.class ) ) );
		byteCodeType.methods.add( byteCodeMethod );
		CodeAttribute code = CodeAttribute.of( 2, 2 );
		byteCodeMethod.attributeSet.addAttribute( code );
		code.ALOAD( 0 );
		code.INVOKESPECIAL( MethodReference.of( MethodReferenceKind.Plain, Object.class, MethodPrototype.of( "<init>", MethodDescriptor.of( void.class ) ) ) );
		code.ALOAD( 0 );
		code.ALOAD( 1 );
		code.PUTFIELD( FieldReference.of( byteCodeType.typeDescriptor(), exitPointField.prototype() ) );
		code.RETURN();
	}

	/*
		│ └─■ [1] ByteCodeMethod accessFlags = [public], prototype = java.lang.Object anycall( mikenakis.intertwine.MethodKey, java.lang.Object[] )
		│   └─■ attributes: 1 items
		│     └─■ [0] CodeAttribute maxStack = 4, maxLocals = 4
	*/
	private static void addUntwinerAnycallMethod( ByteCodeType byteCodeType, ByteCodeField exitPointField )
	{
		ByteCodeMethod byteCodeMethod = ByteCodeMethod.of( ByteCodeMethod.modifierEnum.of( ByteCodeMethod.Modifier.Public ), Anycall.methodPrototype() );
		byteCodeType.methods.add( byteCodeMethod );
		CodeAttribute code = CodeAttribute.of( 4, 4 );
		byteCodeMethod.attributeSet.addAttribute( code );

		/*
		│       │ ├─■ L19:    ALOAD 1
		│       │ ├─■         CHECKCAST mikenakis.test.intertwine.handwritten.HandwrittenKey
		│       │ ├─■         ASTORE 3
		│       │ ├─■ L20:    ALOAD 3
		│       │ ├─■         GETFIELD int mikenakis.test.intertwine.handwritten.HandwrittenKey.index
		│       │ ├─■         TABLESWITCH lowValue = 0, default = L34; 3 items
		│       │ │ ├─■ [0]         L24
		│       │ │ ├─■ [1]         L28
		│       │ │ └─■ [2]         L31
		 */
		code.ALOAD( 1 );
		code.CHECKCAST( TypeDescriptor.of( HandWrittenCompiledKey.class ) );
		code.ASTORE( 3 );
		code.ALOAD( 3 );
		code.GETFIELD( FieldReference.of( HandWrittenCompiledKey.class, FieldPrototype.of( "methodIndex", int.class ) ) );
		TableSwitchInstruction tableSwitchInstruction = code.TABLESWITCH( 0 );

		/*
		│       │ ├─■ L24:    ALOAD 0
		│       │ ├─■         GETFIELD mikenakis.test.intertwine.comparisons.rig.FooInterface mikenakis.test.intertwine.handwritten.HandwrittenUntwiner.exitPoint
		│       │ ├─■         INVOKEINTERFACE reference = Interface void mikenakis.test.intertwine.comparisons.rig.FooInterface.voidMethod(), 1 arguments
		│       │ ├─■ L25:    ACONST_NULL
		│       │ ├─■         ARETURN
		 */
		Instruction l24 = code.ALOAD( 0 );
		tableSwitchInstruction.targetInstructions.add( l24 );
		code.GETFIELD( FieldReference.of( byteCodeType.typeDescriptor(), FieldPrototype.of( "exitPoint", FooInterface.class ) ) );
		code.INVOKEINTERFACE( MethodReference.of( MethodReferenceKind.Interface, FooInterface.class, MethodPrototype.of( "voidMethod", MethodDescriptor.of( void.class ) ) ), 1 );
		code.ACONST_NULL();
		code.ARETURN();

		/*
		│       │ ├─■ L28:    ALOAD 0
		│       │ ├─■         GETFIELD mikenakis.test.intertwine.comparisons.rig.FooInterface mikenakis.test.intertwine.handwritten.HandwrittenUntwiner.exitPoint
		│       │ ├─■         ALOAD 2
		│       │ ├─■         LDC 0
		│       │ ├─■         AALOAD
		│       │ ├─■         CHECKCAST java.lang.Integer
		│       │ ├─■         INVOKEVIRTUAL reference = Plain int java.lang.Integer.intValue()
		│       │ ├─■         INVOKEINTERFACE reference = Interface mikenakis.test.intertwine.comparisons.rig.Alpha mikenakis.test.intertwine.comparisons.rig.FooInterface.getAlpha( int ), 2 arguments
		│       │ ├─■         ARETURN
		*/
		Instruction l28 = code.ALOAD( 0 );
		tableSwitchInstruction.targetInstructions.add( l28 );
		code.GETFIELD( FieldReference.of( byteCodeType.typeDescriptor(), FieldPrototype.of( "exitPoint", FooInterface.class ) ) );
		code.ALOAD( 2 );
		code.LDC( 0 );
		code.AALOAD();
		code.CHECKCAST( TypeDescriptor.of( Integer.class ) );
		code.INVOKEVIRTUAL( MethodReference.of( MethodReferenceKind.Plain, Integer.class, MethodPrototype.of( "intValue", MethodDescriptor.of( TypeDescriptor.of( int.class ) ) ) ) );
		code.INVOKEINTERFACE( MethodReference.of( MethodReferenceKind.Interface, FooInterface.class, MethodPrototype.of( "getAlpha", MethodDescriptor.of( Alpha.class, int.class ) ) ), 2 );
		code.ARETURN();

		/*
		│       │ ├─■ L31:    ALOAD 0
		│       │ ├─■         GETFIELD mikenakis.test.intertwine.comparisons.rig.FooInterface mikenakis.test.intertwine.handwritten.HandwrittenUntwiner.exitPoint
		│       │ ├─■         ALOAD 2
		│       │ ├─■         LDC 0
		│       │ ├─■         AALOAD
		│       │ ├─■         CHECKCAST java.lang.Integer
		│       │ ├─■         INVOKEVIRTUAL reference = Plain int java.lang.Integer.intValue()
		│       │ ├─■         ALOAD 2
		│       │ ├─■         LDC 1
		│       │ ├─■         AALOAD
		│       │ ├─■         CHECKCAST mikenakis.test.intertwine.comparisons.rig.Alpha
		│       │ ├─■         INVOKEINTERFACE reference = Interface void mikenakis.test.intertwine.comparisons.rig.FooInterface.setAlpha( int, mikenakis.test.intertwine.comparisons.rig.Alpha ), 3 arguments
		│       │ ├─■ L32:    ACONST_NULL
		│       │ ├─■         ARETURN
		 */
		Instruction l31 = code.ALOAD( 0 );
		tableSwitchInstruction.targetInstructions.add( l31 );
		code.GETFIELD( FieldReference.of( byteCodeType.typeDescriptor(), FieldPrototype.of( "exitPoint", FooInterface.class ) ) );
		code.ALOAD( 2 );
		code.LDC( 0 );
		code.AALOAD();
		code.CHECKCAST( TypeDescriptor.of( Integer.class ) );
		code.INVOKEVIRTUAL( MethodReference.of( MethodReferenceKind.Plain, Integer.class, MethodPrototype.of( "intValue", MethodDescriptor.of( int.class ) ) ) );
		code.ALOAD( 2 );
		code.LDC( 1 );
		code.AALOAD();
		code.CHECKCAST( TypeDescriptor.of( Alpha.class ) );
		code.INVOKEINTERFACE( MethodReference.of( MethodReferenceKind.Interface, FooInterface.class, MethodPrototype.of( "setAlpha", MethodDescriptor.of( void.class, int.class, Alpha.class ) ) ), 3 );
		code.ACONST_NULL();
		code.ARETURN();

		/*
		│       │ ├─■ L34:    NEW java.lang.AssertionError
		│       │ ├─■         DUP
		│       │ ├─■         ALOAD 3
		│       │ ├─■         INVOKESPECIAL reference = Plain void java.lang.AssertionError.<init>( java.lang.Object )
		│       │ └─■         ATHROW
		 */
		Instruction l34 = code.NEW( TypeDescriptor.of( AssertionError.class ) );
		tableSwitchInstruction.setDefaultInstruction( l34 );
		code.DUP();
		code.ALOAD( 3 );
		code.INVOKESPECIAL( MethodReference.of( MethodReferenceKind.Plain, AssertionError.class, MethodPrototype.of( "<init>", MethodDescriptor.of( void.class, Object.class ) ) ) );
		code.ATHROW();

		/*
		│       ├─■ exceptionInfos: 0 items
		│       └─■ attributeSet: 1 items
		│         └─■ [0] StackMapTableAttribute; 4 items
		│           ├─■ [0] AppendFrame target = L24
		│           │ └─■ localVerificationTypes: 1 items
		│           │   └─■ [0] ObjectVerificationType type = mikenakis.test.intertwine.handwritten.HandwrittenKey
		│           ├─■ [1] SameFrame target = L28
		│           ├─■ [2] SameFrame target = L31
		│           └─■ [3] SameFrame target = L34
		 */
		StackMapTableAttribute stackMapTableAttribute = StackMapTableAttribute.of();
		stackMapTableAttribute.addAppendFrame( l24, ObjectVerificationType.of( TypeDescriptor.of( HandWrittenCompiledKey.class ) ) );
		stackMapTableAttribute.addSameFrame( l28 );
		stackMapTableAttribute.addSameFrame( l31 );
		stackMapTableAttribute.addSameFrame( l34 );
		code.attributeSet.addAttribute( stackMapTableAttribute );

//		for( Method method : FooInterface.class.getMethods() )
//		{
//			int modifiers = method.getModifiers();
//			if( Modifier.isStatic( modifiers ) )
//				continue;
//			if( !Modifier.isAbstract( modifiers ) )
//				continue;
//			assert Modifier.isPublic( modifiers );
//			assert !Modifier.isFinal( modifiers );
//			assert !Modifier.isNative( modifiers );
//			assert !Modifier.isSynchronized( modifiers ); // ?
//			assert !method.isSynthetic(); // ?
//			assert !method.isDefault(); // ?
//			assert !method.isBridge(); // ?
//			if( Kit.get( false ) )
//			{
////				addUntwinerSwitchCase( byteCodeType, exitPointField, //
////					MethodPrototype.of( method.getName(), TypeDescriptor.of( method.getReturnType() ), //
////						Stream.of( method.getParameterTypes() ).map( c -> TypeDescriptor.of( c ) ).toList() ) );
//			}
//		}
	}

	Optional<HandWrittenCompiledKey> tryGetKeyByMethod( Method method )
	{
		return Kit.map.getOptional( keysByMethod, method );
	}

	private static void saveBytes( Path path, byte[] bytes )
	{
		Kit.unchecked( () -> Files.createDirectories( path.getParent() ) );
		Kit.unchecked( () -> Files.write( path, bytes ) );
		Log.debug( "saved bytes: " + path );
	}

	private static String identifierFromTypeName( Class<?> type )
	{
		return type.getName().replace( ".", "_" ).replace( "$", "_" );
	}
}
