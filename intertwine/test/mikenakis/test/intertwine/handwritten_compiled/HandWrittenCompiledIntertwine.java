package mikenakis.test.intertwine.handwritten_compiled;

import mikenakis.bytecode.model.ByteCodeField;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.attributes.CodeAttribute;
import mikenakis.bytecode.model.attributes.StackMapTableAttribute;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.instructions.TableSwitchInstruction;
import mikenakis.bytecode.model.attributes.stackmap.verification.ObjectVerificationType;
import mikenakis.bytecode.model.descriptors.FieldPrototype;
import mikenakis.bytecode.model.descriptors.FieldReference;
import mikenakis.bytecode.model.descriptors.MethodPrototype;
import mikenakis.bytecode.model.descriptors.MethodReference;
import mikenakis.bytecode.model.descriptors.MethodReferenceKind;
import mikenakis.bytecode.printing.ByteCodePrinter;
import mikenakis.intertwine.AnyCall;
import mikenakis.intertwine.Intertwine;
import mikenakis.intertwine.MethodKey;
import mikenakis.java_type_model.FieldDescriptor;
import mikenakis.java_type_model.MethodDescriptor;
import mikenakis.java_type_model.TerminalTypeDescriptor;
import mikenakis.java_type_model.TypeDescriptor;
import mikenakis.kit.Kit;
import mikenakis.kit.logging.Log;
import mikenakis.test.intertwine.rig.Alpha;
import mikenakis.test.intertwine.rig.FooInterface;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Compiling {@link Intertwine}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
class HandWrittenCompiledIntertwine implements Intertwine<FooInterface>
{
	private final HandWrittenCompiledIntertwineFactory factory;
	private final HandWrittenCompiledKey[] keys;
	private final Map<MethodPrototype,HandWrittenCompiledKey> keysByPrototype;
	private final Map<Method,HandWrittenCompiledKey> keysByMethod;
	private static Optional<Class<FooInterface>> cachedEntwinerClass = Optional.empty(); //we have to cache the entwiner class because we create it, and a class cannot be redefined.
	private static Optional<Class<AnyCall<FooInterface>>> cachedUntwinerClass = Optional.empty(); //we have to cache the untwiner class because we create it, and a class cannot be redefined.

	HandWrittenCompiledIntertwine( HandWrittenCompiledIntertwineFactory factory )
	{
		this.factory = factory;
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

	@Override public Collection<MethodKey<FooInterface>> keys()
	{
		return List.of( keys );
	}

	@Override public MethodKey<FooInterface> keyByIndex( int index )
	{
		return keys[index];
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
		│ └─■ [0] mikenakis.test.intertwine.rig.FooInterface
		├─■ extraTypes: 0 items
		├─■ fields: 2 items
		│ ├─■ [0] ByteCodeField accessFlags = [private, final], prototype = mikenakis.test.intertwine.handwritten.HandwrittenKey[] keys
		│ │ └─■ attributeSet: 0 items
		│ └─■ [1] ByteCodeField accessFlags = [private, final], prototype = mikenakis.intertwine.AnyCall exitPoint
		│   └─■ attributeSet: 0 items
		├─■ methods: 4 items
	*/
	@Override public FooInterface newEntwiner( AnyCall<FooInterface> exitPoint )
	{
		Class<FooInterface> entwinerClass = cachedEntwinerClass.orElseGet( () -> {
			Class<FooInterface> c = createEntwinerClass();
			cachedEntwinerClass = Optional.of( c );
			return c;
		} );
		return Kit.unchecked( () -> entwinerClass.getDeclaredConstructor( HandWrittenCompiledKey[].class, AnyCall.class ).newInstance( keys, exitPoint ) );
	}

	private Class<FooInterface> createEntwinerClass()
	{
		TypeDescriptor arrayOfKeyTypeDescriptor = TypeDescriptor.of( HandWrittenCompiledKey[].class );
		TypeDescriptor anyCallTypeDescriptor = TypeDescriptor.of( AnyCall.class );
		ByteCodeType byteCodeType = ByteCodeType.of( //
			ByteCodeType.modifierEnum.of( ByteCodeType.Modifier.Public, ByteCodeType.Modifier.Final, ByteCodeType.Modifier.Super ), //
			TerminalTypeDescriptor.of( "Entwiner_" + identifierFromTypeName( FooInterface.class ) ), //
			Optional.of( TerminalTypeDescriptor.of( Object.class ) ), //
			List.of( TerminalTypeDescriptor.of( FooInterface.class ) ) );
		ByteCodeField keysField = ByteCodeField.of( ByteCodeField.modifierEnum.of( ByteCodeField.Modifier.Private, ByteCodeField.Modifier.Final ), //
			FieldPrototype.of( "keys", FieldDescriptor.of( arrayOfKeyTypeDescriptor ) ) );
		byteCodeType.fields.add( keysField );
		ByteCodeField exitPointField = ByteCodeField.of( ByteCodeField.modifierEnum.of( ByteCodeField.Modifier.Private, ByteCodeField.Modifier.Final ), //
			FieldPrototype.of( "exitPoint", FieldDescriptor.of( anyCallTypeDescriptor ) ) );
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
			{
				addEntwinerInterfaceMethod0( byteCodeType, keysField, exitPointField, //
					MethodPrototype.of( method.getName(), TypeDescriptor.of( method.getReturnType() ), //
						Stream.of( method.getParameterTypes() ).map( c -> TypeDescriptor.of( c ) ).toList() ) );
			}
		}
		addEntwinerInterfaceMethod0( byteCodeType, keysField, exitPointField, MethodPrototype.of( "voidMethod", TypeDescriptor.of( void.class ), List.of() ) );
		addEntwinerInterfaceMethod1( byteCodeType, keysField, exitPointField, MethodPrototype.of( "getAlpha", MethodDescriptor.of( TypeDescriptor.of( "mikenakis.test.intertwine.rig.Alpha" ), TypeDescriptor.of( int.class ) ) ) );
		addEntwinerInterfaceMethod2( byteCodeType, keysField, exitPointField, MethodPrototype.of( "setAlpha", MethodDescriptor.of( TypeDescriptor.of( void.class ), TypeDescriptor.of( int.class ), TypeDescriptor.of( "mikenakis.test.intertwine.rig.Alpha" ) ) ) );

		saveBytes( Path.of( "C:\\temp\\intertwine", byteCodeType.typeDescriptor().typeName + ".class" ), byteCodeType.write() );
		System.out.println( ByteCodePrinter.printByteCodeType( byteCodeType, Optional.empty() ) );

		return factory.byteCodeClassLoader.load( byteCodeType );
	}

	/*
		│ ├─■ [0] ByteCodeMethod accessFlags = [], prototype = void <init>( mikenakis.test.intertwine.handwritten.HandwrittenKey[], mikenakis.intertwine.AnyCall )
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
		│ │     │ ├─■         PUTFIELD mikenakis.intertwine.AnyCall mikenakis.test.intertwine.handwritten.HandwrittenEntwiner.exitPoint
		│ │     │ ├─■ L17: // }
		│ │     │ └─■         RETURN
		│ │     ├─■ exceptionInfos: 0 items
		│ │     └─■ attributeSet: 0 items
	*/
	private static void addEntwinerInitMethod( ByteCodeType byteCodeType, ByteCodeField keysField, ByteCodeField exitPointField )
	{
		ByteCodeMethod method = ByteCodeMethod.of( ByteCodeMethod.modifierEnum.of( ByteCodeMethod.Modifier.Public ), MethodPrototype.of( "<init>", MethodDescriptor.of( TypeDescriptor.of( void.class ), keysField.descriptor().typeDescriptor, exitPointField.descriptor().typeDescriptor ) ) );
		byteCodeType.methods.add( method );
		CodeAttribute code = CodeAttribute.of( 2, 3 );
		method.attributeSet.addAttribute( code );
		code.addALoad( 0 );
		code.addInvokeSpecial( MethodReference.of( MethodReferenceKind.Plain, Object.class, MethodPrototype.of( "<init>", MethodDescriptor.of( void.class ) ) ) );
		code.addALoad( 0 );
		code.addALoad( 1 );
		code.addPutField( FieldReference.of( byteCodeType.typeDescriptor(), keysField.prototype() ) );
		code.addALoad( 0 );
		code.addALoad( 2 );
		code.addPutField( FieldReference.of( byteCodeType.typeDescriptor(), exitPointField.prototype() ) );
		code.addReturn();
	}

	/*
		│ ├─■ [1] ByteCodeMethod accessFlags = [public], prototype = void voidMethod()
		│ │ └─■ attributes: 1 items
		│ │   └─■ [0] CodeAttribute maxStack = 3, maxLocals = 1
		│ │     ├─■ instructions: 10 entries
		│ │     │ ├─■ L21: // exitPoint.anyCall( keys[0], Kit.ARRAY_OF_ZERO_OBJECTS );
		│ │     │ ├─■         ALOAD 0
		│ │     │ ├─■         GETFIELD mikenakis.intertwine.AnyCall mikenakis.test.intertwine.handwritten.HandwrittenEntwiner.exitPoint
		│ │     │ ├─■         ALOAD 0
		│ │     │ ├─■         GETFIELD mikenakis.test.intertwine.handwritten.HandwrittenKey[] mikenakis.test.intertwine.handwritten.HandwrittenEntwiner.keys
		│ │     │ ├─■         LDC 0
		│ │     │ ├─■         AALOAD
		│ │     │ ├─■         GETSTATIC java.lang.Object[] mikenakis.kit.Kit.ARRAY_OF_ZERO_OBJECTS
		│ │     │ ├─■         INVOKEINTERFACE reference = Interface java.lang.Object mikenakis.intertwine.AnyCall.anyCall( mikenakis.intertwine.MethodKey, java.lang.Object[] ), 3 arguments
		│ │     │ ├─■         POP
		│ │     │ ├─■ L22: // }
		│ │     │ └─■         RETURN
		│ │     ├─■ exceptionInfos: 0 items
		│ │     └─■ attributeSet: 0 items
	*/
	private static void addEntwinerInterfaceMethod0( ByteCodeType byteCodeType, ByteCodeField keysField, ByteCodeField exitPointField, MethodPrototype methodPrototype )
	{
		ByteCodeMethod method = ByteCodeMethod.of( ByteCodeMethod.modifierEnum.of( ByteCodeMethod.Modifier.Public ), methodPrototype );
		byteCodeType.methods.add( method );
		CodeAttribute code = CodeAttribute.of( 3, 1 );
		method.attributeSet.addAttribute( code );
		code.addALoad( 0 );
		code.addGetField( FieldReference.of( byteCodeType.typeDescriptor(), exitPointField.prototype() ) );
		code.addALoad( 0 );
		code.addGetField( FieldReference.of( byteCodeType.typeDescriptor(), keysField.prototype() ) );
		code.addLdc( 0 );
		code.addAALoad();
		code.addGetStatic( FieldReference.of( Kit.class, FieldPrototype.of( "ARRAY_OF_ZERO_OBJECTS", Object[].class ) ) );
		code.addInvokeInterface( MethodReference.of( MethodReferenceKind.Interface, AnyCall.class, MethodPrototype.of( "anyCall", MethodDescriptor.of( Object.class, MethodKey.class, Object[].class ) ) ), 3 );
		code.addPop();
		code.addReturn();
	}

	/*
		│ ├─■ [2] ByteCodeMethod accessFlags = [public], prototype = mikenakis.test.intertwine.rig.Alpha getAlpha( int )
		│ │ └─■ attributes: 1 items
		│ │   └─■ [0] CodeAttribute maxStack = 6, maxLocals = 2
		│ │     ├─■ instructions: 16 entries
		│ │     │ ├─■ L26: // return (Alpha)exitPoint.anyCall( keys[1], new Object[] { index } );
		│ │     │ ├─■         ALOAD 0
		│ │     │ ├─■         GETFIELD mikenakis.intertwine.AnyCall mikenakis.test.intertwine.handwritten.HandwrittenEntwiner.exitPoint
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
		│ │     │ ├─■         INVOKEINTERFACE reference = Interface java.lang.Object mikenakis.intertwine.AnyCall.anyCall( mikenakis.intertwine.MethodKey, java.lang.Object[] ), 3 arguments
		│ │     │ ├─■         CHECKCAST mikenakis.test.intertwine.rig.Alpha
		│ │     │ └─■         ARETURN
		│ │     ├─■ exceptionInfos: 0 items
		│ │     └─■ attributeSet: 0 items
	*/
	private static void addEntwinerInterfaceMethod1( ByteCodeType byteCodeType, ByteCodeField keysField, ByteCodeField exitPointField, MethodPrototype methodPrototype )
	{
		ByteCodeMethod method = ByteCodeMethod.of( ByteCodeMethod.modifierEnum.of( ByteCodeMethod.Modifier.Public ), methodPrototype );
		byteCodeType.methods.add( method );
		CodeAttribute code = CodeAttribute.of( 6, 2 );
		method.attributeSet.addAttribute( code );
		code.addALoad( 0 );
		code.addGetField( FieldReference.of( byteCodeType.typeDescriptor(), exitPointField.prototype() ) );
		code.addALoad( 0 );
		code.addGetField( FieldReference.of( byteCodeType.typeDescriptor(), keysField.prototype() ) );
		code.addLdc( 1 );
		code.addAALoad();
		code.addLdc( 1 );
		code.addANewArray( TerminalTypeDescriptor.of( Object.class ) );
		code.addDup();
		code.addLdc( 0 );
		code.addILoad( 1 );
		code.addInvokeStatic( MethodReference.of( MethodReferenceKind.Plain, Integer.class, MethodPrototype.of( "valueOf", MethodDescriptor.of( Integer.class, int.class ) ) ) );
		code.addAAStore();
		code.addInvokeInterface( MethodReference.of( MethodReferenceKind.Interface, AnyCall.class, MethodPrototype.of( "anyCall", MethodDescriptor.of( Object.class, MethodKey.class, Object[].class ) ) ), 3 );
		code.addCheckCast( TypeDescriptor.of( "mikenakis.test.intertwine.rig.Alpha" ) );
		code.addAReturn();
	}

	/*
		│ └─■ [3] ByteCodeMethod accessFlags = [public], prototype = void setAlpha( int, mikenakis.test.intertwine.rig.Alpha )
		│   └─■ attributes: 1 items
		│     └─■ [0] CodeAttribute maxStack = 6, maxLocals = 3
		│       ├─■ instructions: 20 entries
		│       │ ├─■ L31: // exitPoint.anyCall( keys[2], new Object[] { index, alpha } );
		│       │ ├─■         ALOAD 0
		│       │ ├─■         GETFIELD mikenakis.intertwine.AnyCall mikenakis.test.intertwine.handwritten.HandwrittenEntwiner.exitPoint
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
		│       │ ├─■         INVOKEINTERFACE reference = Interface java.lang.Object mikenakis.intertwine.AnyCall.anyCall( mikenakis.intertwine.MethodKey, java.lang.Object[] ), 3 arguments
		│       │ ├─■         POP
		│       │ ├─■ L32: // }
		│       │ └─■         RETURN
		│       ├─■ exceptionInfos: 0 items
		│       └─■ attributeSet: 0 items
		└─■ attributeSet: 0 items
	*/
	private static void addEntwinerInterfaceMethod2( ByteCodeType byteCodeType, ByteCodeField keysField, ByteCodeField exitPointField, MethodPrototype methodPrototype )
	{
		ByteCodeMethod method = ByteCodeMethod.of( ByteCodeMethod.modifierEnum.of( ByteCodeMethod.Modifier.Public ), methodPrototype );
		byteCodeType.methods.add( method );
		CodeAttribute code = CodeAttribute.of( 6, 3 );
		method.attributeSet.addAttribute( code );
		code.addALoad( 0 );
		code.addGetField( FieldReference.of( byteCodeType.typeDescriptor(), exitPointField.prototype() ) );
		code.addALoad( 0 );
		code.addGetField( FieldReference.of( byteCodeType.typeDescriptor(), keysField.prototype() ) );
		code.addLdc( 2 );
		code.addAALoad();
		code.addLdc( 2 );
		code.addANewArray( TerminalTypeDescriptor.of( Object.class ) );
		code.addDup();
		code.addLdc( 0 );
		code.addILoad( 1 );
		code.addInvokeStatic( MethodReference.of( MethodReferenceKind.Plain, Integer.class, MethodPrototype.of( "valueOf", MethodDescriptor.of( Integer.class, int.class ) ) ) );
		code.addAAStore();
		code.addDup();
		code.addLdc( 1 );
		code.addALoad( 2 );
		code.addAAStore();
		code.addInvokeInterface( MethodReference.of( MethodReferenceKind.Interface, AnyCall.class, MethodPrototype.of( "anyCall", MethodDescriptor.of( Object.class, MethodKey.class, Object[].class ) ) ), 3 );
		code.addPop();
		code.addReturn();
	}

	@Override public AnyCall<FooInterface> newUntwiner( FooInterface exitPoint )
	{
		Class<AnyCall<FooInterface>> untwinerClass = cachedUntwinerClass.orElseGet( () -> {
			Class<AnyCall<FooInterface>> c = createUntwinerClass();
			cachedUntwinerClass = Optional.of( c );
			return c;
		} );
		return Kit.unchecked( () -> //
		{
			Constructor<AnyCall<FooInterface>> constructor = untwinerClass.getDeclaredConstructor( FooInterface.class );
			return constructor.newInstance( exitPoint );
		} );
	}

	/*
		mikenakis.classdump.ClassDumpMain.classDump(ClassDumpMain.java:150) | DEBUG | 1 | 16:25:44.815 | main | Dumping C:\Users\MBV\Out\mikenakis\intertwine\test-classes\mikenakis\test\intertwine\handwritten\HandwrittenUntwiner.class
		■ ByteCodeType version = 60.0, accessFlags = [super], this = mikenakis.test.intertwine.handwritten.HandwrittenUntwiner, super = java.lang.Object
		├─■ interfaces: 1 items
		│ └─■ [0] mikenakis.intertwine.AnyCall
		├─■ extraTypes: 0 items
		├─■ fields: 1 items
		│ └─■ [0] ByteCodeField accessFlags = [private, final], prototype = mikenakis.test.intertwine.rig.FooInterface exitPoint
		│   └─■ attributeSet: 0 items
		├─■ methods: 2 items
	*/
	private Class<AnyCall<FooInterface>> createUntwinerClass()
	{
		TypeDescriptor fooInterfaceTypeDescriptor = TypeDescriptor.of( FooInterface.class );
		ByteCodeType byteCodeType = ByteCodeType.of( //
			ByteCodeType.modifierEnum.of( ByteCodeType.Modifier.Public, ByteCodeType.Modifier.Final, ByteCodeType.Modifier.Super ), //
			TerminalTypeDescriptor.of( "Untwiner_" + identifierFromTypeName( FooInterface.class ) ), //
			Optional.of( TerminalTypeDescriptor.of( Object.class ) ), //
			List.of( TerminalTypeDescriptor.of( AnyCall.class ) ) );
		ByteCodeField exitPointField = ByteCodeField.of( ByteCodeField.modifierEnum.of( ByteCodeField.Modifier.Private, ByteCodeField.Modifier.Final ), //
			FieldPrototype.of( "exitPoint", FieldDescriptor.of( fooInterfaceTypeDescriptor ) ) );
		byteCodeType.fields.add( exitPointField );
		addUntwinerInitMethod( byteCodeType, exitPointField );

		addUntwinerAnyCallMethod( byteCodeType, exitPointField );

		saveBytes( Path.of( "C:\\temp\\intertwine", byteCodeType.typeDescriptor().typeName + ".class" ), byteCodeType.write() );
		System.out.println( ByteCodePrinter.printByteCodeType( byteCodeType, Optional.empty() ) );

		return factory.byteCodeClassLoader.load( byteCodeType );
	}

	/*
		│ ├─■ [0] ByteCodeMethod accessFlags = [], prototype = void <init>( mikenakis.test.intertwine.rig.FooInterface )
		│ │ └─■ attributes: 1 items
		│ │   └─■ [0] CodeAttribute maxStack = 2, maxLocals = 2
		│ │     ├─■ instructions: 6 entries
		│ │     │ ├─■ L13:    ALOAD 0
		│ │     │ ├─■         INVOKESPECIAL reference = Plain void java.lang.Object.<init>()
		│ │     │ ├─■ L14:    ALOAD 0
		│ │     │ ├─■         ALOAD 1
		│ │     │ ├─■         PUTFIELD mikenakis.test.intertwine.rig.FooInterface mikenakis.test.intertwine.handwritten.HandwrittenUntwiner.exitPoint
		│ │     │ └─■ L15:    RETURN
		│ │     ├─■ exceptionInfos: 0 items
		│ │     └─■ attributeSet: 0 items
	*/
	private static void addUntwinerInitMethod( ByteCodeType byteCodeType, ByteCodeField exitPointField )
	{
		ByteCodeMethod byteCodeMethod = ByteCodeMethod.of( ByteCodeMethod.modifierEnum.of( ByteCodeMethod.Modifier.Public ), MethodPrototype.of( "<init>", MethodDescriptor.of( void.class, FooInterface.class ) ) );
		byteCodeType.methods.add( byteCodeMethod );
		CodeAttribute code = CodeAttribute.of( 2, 2 );
		byteCodeMethod.attributeSet.addAttribute( code );
		code.addALoad( 0 );
		code.addInvokeSpecial( MethodReference.of( MethodReferenceKind.Plain, Object.class, MethodPrototype.of( "<init>", MethodDescriptor.of( void.class ) ) ) );
		code.addALoad( 0 );
		code.addALoad( 1 );
		code.addPutField( FieldReference.of( byteCodeType.typeDescriptor(), exitPointField.prototype() ) );
		code.addReturn();
	}

	/*
		│ └─■ [1] ByteCodeMethod accessFlags = [public], prototype = java.lang.Object anyCall( mikenakis.intertwine.MethodKey, java.lang.Object[] )
		│   └─■ attributes: 1 items
		│     └─■ [0] CodeAttribute maxStack = 4, maxLocals = 4
	*/
	private static void addUntwinerAnyCallMethod( ByteCodeType byteCodeType, ByteCodeField exitPointField )
	{
		ByteCodeMethod byteCodeMethod = ByteCodeMethod.of( ByteCodeMethod.modifierEnum.of( ByteCodeMethod.Modifier.Public ), MethodPrototype.of( "anyCall", MethodDescriptor.of( Object.class, MethodKey.class, Object[].class ) ) );
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
		code.addALoad( 1 );
		code.addCheckCast( TypeDescriptor.of( HandWrittenCompiledKey.class ) );
		code.addAStore( 3 );
		code.addALoad( 3 );
		code.addGetField( FieldReference.of( HandWrittenCompiledKey.class, FieldPrototype.of( "methodIndex", int.class ) ) );
		TableSwitchInstruction tableSwitchInstruction = code.addTableSwitch( 0 );

		/*
		│       │ ├─■ L24:    ALOAD 0
		│       │ ├─■         GETFIELD mikenakis.test.intertwine.rig.FooInterface mikenakis.test.intertwine.handwritten.HandwrittenUntwiner.exitPoint
		│       │ ├─■         INVOKEINTERFACE reference = Interface void mikenakis.test.intertwine.rig.FooInterface.voidMethod(), 1 arguments
		│       │ ├─■ L25:    ACONST_NULL
		│       │ ├─■         ARETURN
		 */
		Instruction l24 = code.addALoad( 0 );
		tableSwitchInstruction.targetInstructions.add( l24 );
		code.addGetField( FieldReference.of( byteCodeType.typeDescriptor(), FieldPrototype.of( "exitPoint", FooInterface.class ) ) );
		code.addInvokeInterface( MethodReference.of( MethodReferenceKind.Interface, FooInterface.class, MethodPrototype.of( "voidMethod", MethodDescriptor.of( void.class ) ) ), 1 );
		code.addAConstNull();
		code.addAReturn();

		/*
		│       │ ├─■ L28:    ALOAD 0
		│       │ ├─■         GETFIELD mikenakis.test.intertwine.rig.FooInterface mikenakis.test.intertwine.handwritten.HandwrittenUntwiner.exitPoint
		│       │ ├─■         ALOAD 2
		│       │ ├─■         LDC 0
		│       │ ├─■         AALOAD
		│       │ ├─■         CHECKCAST java.lang.Integer
		│       │ ├─■         INVOKEVIRTUAL reference = Plain int java.lang.Integer.intValue()
		│       │ ├─■         INVOKEINTERFACE reference = Interface mikenakis.test.intertwine.rig.Alpha mikenakis.test.intertwine.rig.FooInterface.getAlpha( int ), 2 arguments
		│       │ ├─■         ARETURN
		*/
		Instruction l28 = code.addALoad( 0 );
		tableSwitchInstruction.targetInstructions.add( l28 );
		code.addGetField( FieldReference.of( byteCodeType.typeDescriptor(), FieldPrototype.of( "exitPoint", FooInterface.class ) ) );
		code.addALoad( 2 );
		code.addLdc( 0 );
		code.addAALoad();
		code.addCheckCast( TypeDescriptor.of( Integer.class ) );
		code.addInvokeVirtual( MethodReference.of( MethodReferenceKind.Plain, Integer.class, MethodPrototype.of( "intValue", TypeDescriptor.of( int.class ) ) ) );
		code.addInvokeInterface( MethodReference.of( MethodReferenceKind.Interface, FooInterface.class, MethodPrototype.of( "getAlpha", MethodDescriptor.of( Alpha.class, int.class ) ) ), 2 );
		code.addAReturn();

		/*
		│       │ ├─■ L31:    ALOAD 0
		│       │ ├─■         GETFIELD mikenakis.test.intertwine.rig.FooInterface mikenakis.test.intertwine.handwritten.HandwrittenUntwiner.exitPoint
		│       │ ├─■         ALOAD 2
		│       │ ├─■         LDC 0
		│       │ ├─■         AALOAD
		│       │ ├─■         CHECKCAST java.lang.Integer
		│       │ ├─■         INVOKEVIRTUAL reference = Plain int java.lang.Integer.intValue()
		│       │ ├─■         ALOAD 2
		│       │ ├─■         LDC 1
		│       │ ├─■         AALOAD
		│       │ ├─■         CHECKCAST mikenakis.test.intertwine.rig.Alpha
		│       │ ├─■         INVOKEINTERFACE reference = Interface void mikenakis.test.intertwine.rig.FooInterface.setAlpha( int, mikenakis.test.intertwine.rig.Alpha ), 3 arguments
		│       │ ├─■ L32:    ACONST_NULL
		│       │ ├─■         ARETURN
		 */
		Instruction l31 = code.addALoad( 0 );
		tableSwitchInstruction.targetInstructions.add( l31 );
		code.addGetField( FieldReference.of( byteCodeType.typeDescriptor(), FieldPrototype.of( "exitPoint", FooInterface.class ) ) );
		code.addALoad( 2 );
		code.addLdc( 0 );
		code.addAALoad();
		code.addCheckCast( TypeDescriptor.of( Integer.class ) );
		code.addInvokeVirtual( MethodReference.of( MethodReferenceKind.Plain, Integer.class, MethodPrototype.of( "intValue", MethodDescriptor.of( int.class ) ) ) );
		code.addALoad( 2 );
		code.addLdc( 1 );
		code.addAALoad();
		code.addCheckCast( TypeDescriptor.of( Alpha.class ) );
		code.addInvokeInterface( MethodReference.of( MethodReferenceKind.Interface, FooInterface.class, MethodPrototype.of( "setAlpha", MethodDescriptor.of( void.class, int.class, Alpha.class ) ) ), 3 );
		code.addAConstNull();
		code.addAReturn();

		/*
		│       │ ├─■ L34:    NEW java.lang.AssertionError
		│       │ ├─■         DUP
		│       │ ├─■         ALOAD 3
		│       │ ├─■         INVOKESPECIAL reference = Plain void java.lang.AssertionError.<init>( java.lang.Object )
		│       │ └─■         ATHROW
		 */
		Instruction l34 = code.addNew( TypeDescriptor.of( AssertionError.class ) );
		tableSwitchInstruction.setDefaultInstruction( l34 );
		code.addDup();
		code.addALoad( 3 );
		code.addInvokeSpecial( MethodReference.of( MethodReferenceKind.Plain, AssertionError.class, MethodPrototype.of( "<init>", MethodDescriptor.of( void.class, Object.class ) ) ) );
		code.addAThrow();

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
