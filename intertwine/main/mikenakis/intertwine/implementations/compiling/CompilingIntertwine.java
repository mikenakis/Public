package mikenakis.intertwine.implementations.compiling;

import mikenakis.bytecode.model.ByteCodeField;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.attributes.CodeAttribute;
import mikenakis.bytecode.model.descriptors.FieldPrototype;
import mikenakis.bytecode.model.descriptors.FieldReference;
import mikenakis.bytecode.model.descriptors.MethodPrototype;
import mikenakis.bytecode.model.descriptors.MethodReference;
import mikenakis.bytecode.model.descriptors.MethodReferenceKind;
import mikenakis.intertwine.AnyCall;
import mikenakis.intertwine.Intertwine;
import mikenakis.intertwine.MethodKey;
import mikenakis.java_type_model.FieldDescriptor;
import mikenakis.java_type_model.TerminalTypeDescriptor;
import mikenakis.java_type_model.TypeDescriptor;
import mikenakis.kit.Kit;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
class CompilingIntertwine<T> implements Intertwine<T>
{
	private static final FieldReference arrayOfZeroObjectsFieldReference = FieldReference.of( TypeDescriptor.of( Kit.class ), FieldPrototype.of( "ARRAY_OF_ZERO_OBJECTS", Object[].class ) );

	private static final MethodReference anyCallMethodReference = MethodReference.of( MethodReferenceKind.Interface, TypeDescriptor.of( AnyCall.class ), //
		MethodPrototype.of( "anyCall", TypeDescriptor.of( MethodKey.class ), TypeDescriptor.of( Object[].class ) ) );

	private static final MethodReference boxIntMethodReference = MethodReference.of( MethodReferenceKind.Plain, TypeDescriptor.of( Integer.class ), //
		MethodPrototype.of( "valueOf", TypeDescriptor.of( Integer.class ), TypeDescriptor.of( int.class ) ) );

	private final Class<? super T> interfaceType;
	private final List<CompilingKey<T>> keys;
	private final Map<MethodPrototype,CompilingKey<T>> keysByPrototype;
	private final Map<Method,CompilingKey<T>> keysByMethod;

	CompilingIntertwine( Class<? super T> interfaceType )
	{
		assert interfaceType.isInterface();
		if( !Modifier.isPublic( interfaceType.getModifiers() ) )
			throw new RuntimeException( new IllegalAccessException() );
		this.interfaceType = interfaceType;
		Method[] methods = interfaceType.getMethods();
		keys = IntStream.range( 0, methods.length ).mapToObj( i -> createKey( methods[i], i ) ).collect( Collectors.toList() );
		keysByPrototype = keys.stream().collect( Collectors.toMap( k -> k.methodPrototype, k -> k ) );
		keysByMethod = keys.stream().collect( Collectors.toMap( k -> k.method, k -> k ) );
	}

	private CompilingKey<T> createKey( Method method, int index )
	{
		MethodPrototype methodPrototype = MethodPrototype.of( method );
		return new CompilingKey<>( this, method, methodPrototype, index );
	}

	@Override public Class<? super T> interfaceType()
	{
		return interfaceType;
	}

	@Override public Collection<MethodKey<T>> keys()
	{
		return Kit.collection.downCast( keys );
	}

	@Override public MethodKey<T> keyByIndex( int index )
	{
		return keys.get( index );
	}

	@Override public MethodKey<T> keyByMethodPrototype( MethodPrototype methodPrototype )
	{
		return Kit.map.get( keysByPrototype, methodPrototype );
	}

	private static String identifierFromTypeName( Class<?> type )
	{
		return type.getName().replace( ".", "_" ).replace( "$", "_" );
	}

	/*
		mikenakis.classdump.ClassDumpMain.classDump(ClassDumpMain.java:151) | DEBUG | 1 | 14:37:49.330 | main | Dumping C:\Users\MBV\Out\mikenakis\intertwine\test-classes\mikenakis\test\intertwine\handwritten\HandwrittenEntwiner.class (Optional[C:\Users\MBV\Personal\IdeaProjects\mikenakis-personal3\Public\intertwine\test\mikenakis\test\intertwine\handwritten])
		■ ByteCodeType version = 60.0, accessFlags = [final, super], this = mikenakis.test.intertwine.handwritten.HandwrittenEntwiner, super = java.lang.Object
		├─■ interfaces: 1 items
		│ └─■ [0] mikenakis.test.intertwine.rig.FooInterface
		├─■ extraTypes: 0 items
		├─■ fields: 2 items
		│ ├─■ [0] ByteCodeField accessFlags = [private, final], prototype = mikenakis.test.intertwine.handwritten.HandwrittenKey[] keys
		│ │ └─■ attributeSet: 0 items
		│ └─■ [1] ByteCodeField accessFlags = [private, final], prototype = mikenakis.intertwine.AnyCall exitPoint
		│   └─■ attributeSet: 1 items
		│     └─■ [0] SignatureAttribute Lmikenakis/intertwine/AnyCall<Lmikenakis/test/intertwine/rig/FooInterface;>;
		├─■ methods: 4 items
		*/
	@Override public T newEntwiner( AnyCall<T> exitPoint )
	{
		TypeDescriptor arrayOfCompilingKeyTypeDescriptor = TypeDescriptor.of( CompilingKey[].class );
		TypeDescriptor anyCallTypeDescriptor = TypeDescriptor.of( AnyCall.class );
		ByteCodeType byteCodeType = ByteCodeType.of( ByteCodeType.modifierEnum.of( ByteCodeType.Modifier.Final, ByteCodeType.Modifier.Super ), //
			TerminalTypeDescriptor.of( "Entwiner_" + identifierFromTypeName( interfaceType ) ), //
			Optional.of( TerminalTypeDescriptor.of( Object.class ) ), //
			List.of( TerminalTypeDescriptor.of( interfaceType ) ) );
		ByteCodeField keysField = ByteCodeField.of( ByteCodeField.modifierEnum.of( ByteCodeField.Modifier.Private, ByteCodeField.Modifier.Final ), //
			FieldPrototype.of( "keys", FieldDescriptor.of( arrayOfCompilingKeyTypeDescriptor ) ) );
		byteCodeType.fields.add( keysField );
		ByteCodeField exitPointField = ByteCodeField.of( ByteCodeField.modifierEnum.of( ByteCodeField.Modifier.Private, ByteCodeField.Modifier.Final ), //
			FieldPrototype.of( "exitPoint", FieldDescriptor.of( anyCallTypeDescriptor ) ) );
		byteCodeType.fields.add( exitPointField );
		addInitMethod( byteCodeType, keysField, exitPointField );

		for( Method method : interfaceType.getMethods() )
		{
			int modifiers = method.getModifiers();
			if( java.lang.reflect.Modifier.isStatic( modifiers ) )
				continue;
			if( !java.lang.reflect.Modifier.isAbstract( modifiers ) )
				continue;
			assert java.lang.reflect.Modifier.isPublic( modifiers );
			assert !java.lang.reflect.Modifier.isFinal( modifiers );
			assert !java.lang.reflect.Modifier.isNative( modifiers );
			assert !java.lang.reflect.Modifier.isSynchronized( modifiers ); // ?
			assert !method.isSynthetic(); // ?
			assert !method.isDefault(); // ?
			assert !method.isBridge(); // ?
			if( Kit.get( false ) )
			{
				addInterfaceMethod0( byteCodeType, keysField, exitPointField, //
					MethodPrototype.of( method.getName(), TypeDescriptor.of( method.getReturnType() ), //
						Stream.of( method.getParameterTypes() ).map( c -> TypeDescriptor.of( c ) ).toList() ) );
			}
		}
		addInterfaceMethod0( byteCodeType, keysField, exitPointField, //
			MethodPrototype.of( "voidMethod", TypeDescriptor.of( void.class ), //
				List.of() ) );
		addInterfaceMethod1( byteCodeType, keysField, exitPointField, //
			MethodPrototype.of( "getAlpha", TypeDescriptor.of( "mikenakis.test.intertwine.rig.Alpha" ), //
				TypeDescriptor.of( int.class ) ) );
		addInterfaceMethod2( byteCodeType, keysField, exitPointField, //
			MethodPrototype.of( "setAlpha", TypeDescriptor.of( void.class ), //
				TypeDescriptor.of( int.class ), TypeDescriptor.of( "mikenakis.test.intertwine.rig.Alpha" ) ) );

		return new CompilingEntwiner<>( this, exitPoint ).entryPoint;
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
		│ │     │ ├─■         INVOKEINTERFACE interfaceMethodReferenceConstant = kind = Interface, reference =  java.lang.Object mikenakis.intertwine.AnyCall.anyCall( mikenakis.intertwine.MethodKey, java.lang.Object[] ), 3 arguments
		│ │     │ ├─■         POP
		│ │     │ ├─■ L22: // }
		│ │     │ └─■         RETURN
		│ │     ├─■ exceptionInfos: 0 items
		│ │     └─■ attributeSet: 2 items
		│ │       ├─■ [0] LineNumberTableAttribute; 2 items
		│ │       │ ├─■ [0] LineNumberTableEntry lineNumber = 21, start = L21
		│ │       │ └─■ [1] LineNumberTableEntry lineNumber = 22, start = L22
		│ │       └─■ [1] LocalVariableTableAttribute; 1 items
		│ │         └─■ [0] LocalVariableTableEntry index = 0, start = L21, end = @end, prototype = mikenakis.test.intertwine.handwritten.HandwrittenEntwiner this
	*/
	private static void addInterfaceMethod0( ByteCodeType byteCodeType, ByteCodeField keysField, ByteCodeField exitPointField, MethodPrototype methodPrototype )
	{
		ByteCodeMethod method = ByteCodeMethod.of( ByteCodeMethod.modifierEnum.of( ByteCodeMethod.Modifier.Public ), methodPrototype );
		byteCodeType.methods.add( method );
		CodeAttribute code = CodeAttribute.of( 2, 3 );
		method.attributeSet.addAttribute( code );
		code.addALoad( 0 );
		code.addGetField( FieldReference.of( byteCodeType.typeDescriptor(), exitPointField.prototype() ) );
		code.addALoad( 0 );
		code.addGetField( FieldReference.of( byteCodeType.typeDescriptor(), keysField.prototype() ) );
		code.addLdc( 0 );
		code.addAALoad();
		code.addGetStatic( arrayOfZeroObjectsFieldReference );
		code.addInvokeInterface( anyCallMethodReference, 3 );
		code.addPop();
		code.addReturn();
	}

	/*
		│ ├─■ [2] ByteCodeMethod accessFlags = [public], prototype = mikenakis.test.intertwine.rig.Alpha getAlpha( int )
		│ │ └─■ attributes: 2 items
		│ │   ├─■ [0] CodeAttribute maxStack = 6, maxLocals = 2
		│ │   │ ├─■ instructions: 16 entries
		│ │   │ │ ├─■ L26: // return (Alpha)exitPoint.anyCall( keys[1], new Object[] { index } );
		│ │   │ │ ├─■         ALOAD 0
		│ │   │ │ ├─■         GETFIELD mikenakis.intertwine.AnyCall mikenakis.test.intertwine.handwritten.HandwrittenEntwiner.exitPoint
		│ │   │ │ ├─■         ALOAD 0
		│ │   │ │ ├─■         GETFIELD mikenakis.test.intertwine.handwritten.HandwrittenKey[] mikenakis.test.intertwine.handwritten.HandwrittenEntwiner.keys
		│ │   │ │ ├─■         LDC 1
		│ │   │ │ ├─■         AALOAD
		│ │   │ │ ├─■         LDC 1
		│ │   │ │ ├─■         ANEWARRAY java.lang.Object
		│ │   │ │ ├─■         DUP
		│ │   │ │ ├─■         LDC 0
		│ │   │ │ ├─■         ILOAD 1
		│ │   │ │ ├─■         INVOKESTATIC kind = Plain, reference =  java.lang.Integer java.lang.Integer.valueOf( int )
		│ │   │ │ ├─■         AASTORE
		│ │   │ │ ├─■         INVOKEINTERFACE interfaceMethodReferenceConstant = kind = Interface, reference =  java.lang.Object mikenakis.intertwine.AnyCall.anyCall( mikenakis.intertwine.MethodKey, java.lang.Object[] ), 3 arguments
		│ │   │ │ ├─■         CHECKCAST mikenakis.test.intertwine.rig.Alpha
		│ │   │ │ └─■         ARETURN
		│ │   │ ├─■ exceptionInfos: 0 items
		│ │   │ └─■ attributeSet: 2 items
		│ │   │   ├─■ [0] LineNumberTableAttribute; 1 items
		│ │   │   │ └─■ [0] LineNumberTableEntry lineNumber = 26, start = L26
		│ │   │   └─■ [1] LocalVariableTableAttribute; 2 items
		│ │   │     ├─■ [0] LocalVariableTableEntry index = 0, start = L26, end = @end, prototype = mikenakis.test.intertwine.handwritten.HandwrittenEntwiner this
		│ │   │     └─■ [1] LocalVariableTableEntry index = 1, start = L26, end = @end, prototype = int index
		│ │   └─■ [1] MethodParametersAttribute; 1 items
		│ │     └─■ [0] MethodParameter accessFlags = [], name = "index"
	*/
	private static void addInterfaceMethod1( ByteCodeType byteCodeType, ByteCodeField keysField, ByteCodeField exitPointField, MethodPrototype methodPrototype )
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
		code.addInvokeStatic( boxIntMethodReference );
		code.addAAStore();
		code.addInvokeInterface( anyCallMethodReference, 3 );
		code.addCheckCast( TypeDescriptor.of( "mikenakis.test.intertwine.rig.Alpha" ) );
		code.addReturn();
	}

	/*
		│ └─■ [3] ByteCodeMethod accessFlags = [public], prototype = void setAlpha( int, mikenakis.test.intertwine.rig.Alpha )
		│   └─■ attributes: 2 items
		│     ├─■ [0] CodeAttribute maxStack = 6, maxLocals = 3
		│     │ ├─■ instructions: 20 entries
		│     │ │ ├─■ L31: // exitPoint.anyCall( keys[2], new Object[] { index, alpha } );
		│     │ │ ├─■         ALOAD 0
		│     │ │ ├─■         GETFIELD mikenakis.intertwine.AnyCall mikenakis.test.intertwine.handwritten.HandwrittenEntwiner.exitPoint
		│     │ │ ├─■         ALOAD 0
		│     │ │ ├─■         GETFIELD mikenakis.test.intertwine.handwritten.HandwrittenKey[] mikenakis.test.intertwine.handwritten.HandwrittenEntwiner.keys
		│     │ │ ├─■         LDC 2
		│     │ │ ├─■         AALOAD
		│     │ │ ├─■         LDC 2
		│     │ │ ├─■         ANEWARRAY java.lang.Object
		│     │ │ ├─■         DUP
		│     │ │ ├─■         LDC 0
		│     │ │ ├─■         ILOAD 1
		│     │ │ ├─■         INVOKESTATIC kind = Plain, reference =  java.lang.Integer java.lang.Integer.valueOf( int )
		│     │ │ ├─■         AASTORE
		│     │ │ ├─■         DUP
		│     │ │ ├─■         LDC 1
		│     │ │ ├─■         ALOAD 2
		│     │ │ ├─■         AASTORE
		│     │ │ ├─■         INVOKEINTERFACE interfaceMethodReferenceConstant = kind = Interface, reference =  java.lang.Object mikenakis.intertwine.AnyCall.anyCall( mikenakis.intertwine.MethodKey, java.lang.Object[] ), 3 arguments
		│     │ │ ├─■         POP
		│     │ │ ├─■ L32: // }
		│     │ │ └─■         RETURN
		│     │ ├─■ exceptionInfos: 0 items
		│     │ └─■ attributeSet: 2 items
		│     │   ├─■ [0] LineNumberTableAttribute; 2 items
		│     │   │ ├─■ [0] LineNumberTableEntry lineNumber = 31, start = L31
		│     │   │ └─■ [1] LineNumberTableEntry lineNumber = 32, start = L32
		│     │   └─■ [1] LocalVariableTableAttribute; 3 items
		│     │     ├─■ [0] LocalVariableTableEntry index = 0, start = L31, end = @end, prototype = mikenakis.test.intertwine.handwritten.HandwrittenEntwiner this
		│     │     ├─■ [1] LocalVariableTableEntry index = 1, start = L31, end = @end, prototype = int index
		│     │     └─■ [2] LocalVariableTableEntry index = 2, start = L31, end = @end, prototype = mikenakis.test.intertwine.rig.Alpha alpha
		│     └─■ [1] MethodParametersAttribute; 2 items
		│       ├─■ [0] MethodParameter accessFlags = [], name = "index"
		│       └─■ [1] MethodParameter accessFlags = [], name = "alpha"
		└─■ attributeSet: 1 items
		  └─■ [0] SourceFileAttribute "HandwrittenEntwiner.java"
	*/
	private static void addInterfaceMethod2( ByteCodeType byteCodeType, ByteCodeField keysField, ByteCodeField exitPointField, MethodPrototype methodPrototype )
	{
		ByteCodeMethod method = ByteCodeMethod.of( ByteCodeMethod.modifierEnum.of( ByteCodeMethod.Modifier.Public ), methodPrototype );
		byteCodeType.methods.add( method );
		CodeAttribute code = CodeAttribute.of( 6, 2 );
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
		code.addInvokeStatic( boxIntMethodReference );
		code.addAAStore();
		code.addDup();
		code.addLdc( 1 );
		code.addALoad( 2 );
		code.addAAStore();
		code.addInvokeInterface( anyCallMethodReference, 3 );
		code.addPop();
		code.addReturn();
	}

	/*
		│ ├─■ [0] ByteCodeMethod accessFlags = [], prototype = void <init>( mikenakis.test.intertwine.handwritten.HandwrittenKey[], mikenakis.intertwine.AnyCall )
		│ │ └─■ attributes: 3 items
		│ │   ├─■ [0] CodeAttribute maxStack = 2, maxLocals = 3
		│ │   │ ├─■ instructions: 9 entries
		│ │   │ │ ├─■ L14: // {
		│ │   │ │ ├─■         ALOAD 0
		│ │   │ │ ├─■         INVOKESPECIAL kind = Plain, reference =  void java.lang.Object.<init>()
		│ │   │ │ ├─■ L15: // this.keys = keys;
		│ │   │ │ ├─■         ALOAD 0
		│ │   │ │ ├─■         ALOAD 1
		│ │   │ │ ├─■         PUTFIELD mikenakis.test.intertwine.handwritten.HandwrittenKey[] mikenakis.test.intertwine.handwritten.HandwrittenEntwiner.keys
		│ │   │ │ ├─■ L16: // this.exitPoint = exitPoint;
		│ │   │ │ ├─■         ALOAD 0
		│ │   │ │ ├─■         ALOAD 2
		│ │   │ │ ├─■         PUTFIELD mikenakis.intertwine.AnyCall mikenakis.test.intertwine.handwritten.HandwrittenEntwiner.exitPoint
		│ │   │ │ ├─■ L17: // }
		│ │   │ │ └─■         RETURN
		│ │   │ ├─■ exceptionInfos: 0 items
		│ │   │ └─■ attributeSet: 3 items
		│ │   │   ├─■ [0] LineNumberTableAttribute; 4 items
		│ │   │   │ ├─■ [0] LineNumberTableEntry lineNumber = 14, start = L14
		│ │   │   │ ├─■ [1] LineNumberTableEntry lineNumber = 15, start = L15
		│ │   │   │ ├─■ [2] LineNumberTableEntry lineNumber = 16, start = L16
		│ │   │   │ └─■ [3] LineNumberTableEntry lineNumber = 17, start = L17
		│ │   │   ├─■ [1] LocalVariableTableAttribute; 3 items
		│ │   │   │ ├─■ [0] LocalVariableTableEntry index = 0, start = L14, end = @end, prototype = mikenakis.test.intertwine.handwritten.HandwrittenEntwiner this
		│ │   │   │ ├─■ [1] LocalVariableTableEntry index = 1, start = L14, end = @end, prototype = mikenakis.test.intertwine.handwritten.HandwrittenKey[] keys
		│ │   │   │ └─■ [2] LocalVariableTableEntry index = 2, start = L14, end = @end, prototype = mikenakis.intertwine.AnyCall exitPoint
		│ │   │   └─■ [2] LocalVariableTypeTableAttribute; 1 items
		│ │   │     └─■ [0] LocalVariableTypeTableEntry index = 2, start = L14, end = @end, name = "exitPoint", signature = "Lmikenakis/intertwine/AnyCall<Lmikenakis/test/intertwine/rig/FooInterface;>;" (exitPoint Lmikenakis/intertwine/AnyCall<Lmikenakis/test/intertwine/rig/FooInterface;>;)
		│ │   ├─■ [1] MethodParametersAttribute; 2 items
		│ │   │ ├─■ [0] MethodParameter accessFlags = [], name = "keys"
		│ │   │ └─■ [1] MethodParameter accessFlags = [], name = "exitPoint"
		│ │   └─■ [2] SignatureAttribute ([Lmikenakis/test/intertwine/handwritten/HandwrittenKey;Lmikenakis/intertwine/AnyCall<Lmikenakis/test/intertwine/rig/FooInterface;>;)V
	*/
	private static void addInitMethod( ByteCodeType byteCodeType, ByteCodeField keysField, ByteCodeField exitPointField )
	{
		ByteCodeMethod method = ByteCodeMethod.of( ByteCodeMethod.modifierEnum.of(), //
			MethodPrototype.of( "<init>", TypeDescriptor.of( void.class ), keysField.descriptor().typeDescriptor, exitPointField.descriptor().typeDescriptor ) );
		byteCodeType.methods.add( method );
		CodeAttribute code = CodeAttribute.of( 2, 3 );
		method.attributeSet.addAttribute( code );
		code.addALoad( 0 );
		code.addInvokeSpecial( MethodReference.of( MethodReferenceKind.Plain, TypeDescriptor.of( Object.class ), MethodPrototype.of( "<init>", TypeDescriptor.of( void.class ) ) ) );
		code.addALoad( 0 );
		code.addALoad( 1 );
		code.addPutField( FieldReference.of( byteCodeType.typeDescriptor(), keysField.prototype() ) );
		code.addALoad( 0 );
		code.addALoad( 2 );
		code.addPutField( FieldReference.of( byteCodeType.typeDescriptor(), exitPointField.prototype() ) );
		code.addReturn();
	}

	@Override public AnyCall<T> newUntwiner( T exitPoint )
	{
		return new CompilingUntwiner<>( this, exitPoint ).anycall;
	}

	Optional<CompilingKey<T>> tryGetKeyByMethod( Method method )
	{
		return Kit.map.getOptional( keysByMethod, method );
	}
}
