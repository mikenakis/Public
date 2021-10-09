package mikenakis.intertwine.implementations.compiling;

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
import mikenakis.kit.functional.Function1;
import mikenakis.kit.functional.Function2;
import mikenakis.kit.logging.Log;

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
class CompilingIntertwine<T> implements Intertwine<T>
{
	private final CompilingIntertwineFactory factory;
	private final Class<? super T> interfaceType0;
	private final ByteCodeType interfaceByteCodeType;
	private final CompilingKey<T>[] keys;
	private final Map<MethodPrototype,CompilingKey<T>> keysByPrototype;

	CompilingIntertwine( CompilingIntertwineFactory factory, Class<? super T> interfaceType )
	{
		assert interfaceType.isInterface();
		if( !Modifier.isPublic( interfaceType.getModifiers() ) )
			throw new RuntimeException( new IllegalAccessException() );
		this.factory = factory;
		interfaceType0 = interfaceType;
		interfaceByteCodeType = ByteCodeType.read( interfaceType );
		Method[] methods = interfaceType.getMethods();
		keys = buildArrayOfKey( methods );
		keysByPrototype = Stream.of( keys ).collect( Collectors.toMap( k -> k.methodPrototype, k -> k ) );
	}

	private CompilingKey<T>[] buildArrayOfKey( Method[] methods )
	{
		// IntellijIdea blooper: IntellijIdea thinks that the following code only needs the "unchecked" suppression. Javac thinks differently.
		// In order to keep Javac happy, the raw-types suppression must be added,
		// but then IntellijIdea thinks that raw-types is redundant and complains about it,
		// so we need to also suppress the "redundant suppression" inspection of IntellijIdea.
		//noinspection RedundantSuppression
		@SuppressWarnings( { "unchecked", "rawtypes" } ) CompilingKey<T>[] result = IntStream.range( 0, methods.length ) //
			.mapToObj( i -> createKey( methods[i], i ) ) //
			.toArray( n -> new CompilingKey[n] );
		return result;
	}

	private CompilingKey<T> createKey( Method method, int index )
	{
		return new CompilingKey<>( this, MethodPrototype.of( method ), index );
	}

	@Override public Class<? super T> interfaceType()
	{
		return interfaceType0;
	}

	@Override public Collection<MethodKey<T>> keys()
	{
		return List.of( keys );
	}

	@Override public MethodKey<T> keyByIndex( int index )
	{
		return keys[index];
	}

	@Override public MethodKey<T> keyByMethodPrototype( MethodPrototype methodPrototype )
	{
		return Kit.map.get( keysByPrototype, methodPrototype );
	}

	@Override public T newEntwiner( AnyCall<T> exitPoint )
	{
		Class<T> entwinerClass = createEntwinerClass();
		Constructor<T> constructor = Kit.unchecked( () -> entwinerClass.getDeclaredConstructor( CompilingKey[].class, AnyCall.class ) );
		return Kit.unchecked( () -> constructor.newInstance( keys, exitPoint ) );
	}

	@Override public AnyCall<T> newUntwiner( T exitPoint )
	{
		Class<T> untwinerClass = createUntwinerClass();
		Constructor<T> constructor = Kit.unchecked( () -> untwinerClass.getDeclaredConstructor( interfaceType0 ) );
		@SuppressWarnings( "unchecked" ) AnyCall<T> result = (AnyCall<T>)Kit.unchecked( () -> constructor.newInstance( exitPoint ) );
		return result;
	}

	private Class<T> createEntwinerClass()
	{
		ByteCodeType byteCodeType = ByteCodeType.of( //
			ByteCodeType.modifierEnum.of( ByteCodeType.Modifier.Public, ByteCodeType.Modifier.Final, ByteCodeType.Modifier.Super ), //
			TerminalTypeDescriptor.of( "Entwiner_" + identifierFromTypeName( interfaceType0 ) ), //
			Optional.of( TerminalTypeDescriptor.of( Object.class ) ), //
			List.of( interfaceByteCodeType.typeDescriptor() ) );
		ByteCodeField keysField = ByteCodeField.of( ByteCodeField.modifierEnum.of( ByteCodeField.Modifier.Private, ByteCodeField.Modifier.Final ), //
			FieldPrototype.of( "keys", FieldDescriptor.of( CompilingKey[].class ) ) );
		byteCodeType.fields.add( keysField );
		ByteCodeField exitPointField = ByteCodeField.of( ByteCodeField.modifierEnum.of( ByteCodeField.Modifier.Private, ByteCodeField.Modifier.Final ), //
			FieldPrototype.of( "exitPoint", FieldDescriptor.of( AnyCall.class ) ) );
		byteCodeType.fields.add( exitPointField );
		addEntwinerInitMethod( byteCodeType, keysField, exitPointField );

		List<ByteCodeMethod> methods = interfaceByteCodeType.methods;
		for( int methodIndex = 0; methodIndex < methods.size(); methodIndex++ )
		{
			ByteCodeMethod method = methods.get( methodIndex );
			if( method.modifiers.contains( ByteCodeMethod.Modifier.Static ) )
				continue;
			if( !method.modifiers.contains( ByteCodeMethod.Modifier.Abstract ) )
				continue;
			assert method.modifiers.contains( ByteCodeMethod.Modifier.Public );
			assert !method.modifiers.contains( ByteCodeMethod.Modifier.Final );
			assert !method.modifiers.contains( ByteCodeMethod.Modifier.Native );
			assert !method.modifiers.contains( ByteCodeMethod.Modifier.Synchronized ); // ?
			assert !method.modifiers.contains( ByteCodeMethod.Modifier.Synthetic ); // ?
			assert !method.modifiers.contains( ByteCodeMethod.Modifier.Bridge ); // ?
			addEntwinerInterfaceMethod( byteCodeType, keysField, exitPointField, methodIndex, method.prototype() );
		}

		saveBytes( Path.of( "C:\\temp\\intertwine", byteCodeType.typeDescriptor().typeName + ".class" ), byteCodeType.write() );
		System.out.println( ByteCodePrinter.printByteCodeType( byteCodeType, Optional.empty() ) );

		return factory.byteCodeClassLoader.load( byteCodeType );
	}

	private static void addEntwinerInterfaceMethod( ByteCodeType byteCodeType, ByteCodeField keysField, ByteCodeField exitPointField, int methodIndex, //
		MethodPrototype methodPrototype )
	{
		ByteCodeMethod byteCodeMethod = ByteCodeMethod.of( ByteCodeMethod.modifierEnum.of( ByteCodeMethod.Modifier.Public ), methodPrototype );
		byteCodeType.methods.add( byteCodeMethod );
		CodeAttribute code = CodeAttribute.of( 6, 3 );
		byteCodeMethod.attributeSet.addAttribute( code );

		code.ALOAD( 0 ); //push this
		code.GETFIELD( FieldReference.of( byteCodeType.typeDescriptor(), exitPointField.prototype() ) ); //pop this, push this->exitPoint

		code.ALOAD( 0 ); //push this
		code.GETFIELD( FieldReference.of( byteCodeType.typeDescriptor(), keysField.prototype() ) ); //pop this, push keys
		code.LDC( methodIndex ); //push methodIndex
		code.AALOAD(); //pop methodIndex, pop keys, push keys[methodIndex]

		code.LDC( methodPrototype.parameterCount() ); //push method-parameter-count
		code.ANEWARRAY( TerminalTypeDescriptor.of( Object.class ) ); //pop method-parameter-count, push new Object[]

		for( int parameterIndex = 0; parameterIndex < methodPrototype.parameterCount(); parameterIndex++ )
		{
			TypeDescriptor parameterTypeDescriptor = methodPrototype.descriptor.parameterTypeDescriptors.get( parameterIndex );
			code.DUP(); //pop Object[], push Object[], Object[]
			code.LDC( parameterIndex ); //push parameterIndex
			if( parameterTypeDescriptor.isPrimitive() )
			{
				PrimitiveTypeInfo primitiveTypeInfo = Kit.map.get( primitiveTypeInfosByTypeDescriptor, parameterTypeDescriptor );
				primitiveTypeInfo.loadInstructionFactory.invoke( code, 1 + parameterIndex );
				code.INVOKESTATIC( primitiveTypeInfo.boxingMethod );
			}
			else
				code.ALOAD( 1 + parameterIndex ); //push parameter (from parameters[parameterIndex])
			code.AASTORE(); //pop parameter, pop parameterIndex, pop Object[], set Object[parameterIndex] = parameter
		}

		code.INVOKEINTERFACE( MethodReference.of( MethodReferenceKind.Interface, AnyCall.class, //
			MethodPrototype.of( "anyCall", MethodDescriptor.of( Object.class, MethodKey.class, Object[].class ) ) ), 3 );

		TypeDescriptor returnTypeDescriptor = methodPrototype.descriptor.returnTypeDescriptor;
		if( returnTypeDescriptor.equals( TypeDescriptor.of( void.class ) ) )
		{
			code.POP();
			code.RETURN();
		}
		else if( returnTypeDescriptor.isPrimitive() )
		{
			PrimitiveTypeInfo primitiveTypeInfo = Kit.map.get( primitiveTypeInfosByTypeDescriptor, returnTypeDescriptor );
			code.INVOKEVIRTUAL( primitiveTypeInfo.unboxingMethod );
			primitiveTypeInfo.returnInstructionFactory.invoke( code );
		}
		else
		{
			code.CHECKCAST( methodPrototype.descriptor.returnTypeDescriptor );
			code.ARETURN();
		}
	}

	private static void addEntwinerInitMethod( ByteCodeType byteCodeType, ByteCodeField keysField, ByteCodeField exitPointField )
	{
		ByteCodeMethod byteCodeMethod = ByteCodeMethod.of( //
			ByteCodeMethod.modifierEnum.of( ByteCodeMethod.Modifier.Public ), //
			MethodPrototype.of( "<init>", //
				MethodDescriptor.of( TypeDescriptor.of( void.class ), keysField.descriptor().typeDescriptor, exitPointField.descriptor().typeDescriptor ) ) );
		byteCodeType.methods.add( byteCodeMethod );
		CodeAttribute code = CodeAttribute.of( 2, 3 );
		byteCodeMethod.attributeSet.addAttribute( code );
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

	private Class<T> createUntwinerClass()
	{
		ByteCodeType byteCodeType = ByteCodeType.of( //
			ByteCodeType.modifierEnum.of( ByteCodeType.Modifier.Public, ByteCodeType.Modifier.Final, ByteCodeType.Modifier.Super ), //
			TerminalTypeDescriptor.of( "Untwiner_" + identifierFromTypeName( interfaceType0 ) ), //
			Optional.of( TerminalTypeDescriptor.of( Object.class ) ), //
			List.of( TerminalTypeDescriptor.of( AnyCall.class ) ) );
		ByteCodeField exitPointField = ByteCodeField.of( ByteCodeField.modifierEnum.of( ByteCodeField.Modifier.Private, ByteCodeField.Modifier.Final ), //
			FieldPrototype.of( "exitPoint", FieldDescriptor.of( interfaceByteCodeType.typeDescriptor() ) ) );
		byteCodeType.fields.add( exitPointField );
		addUntwinerInitMethod( byteCodeType, interfaceByteCodeType, exitPointField );
		addUntwinerAnyCallMethod( byteCodeType, interfaceByteCodeType, exitPointField );

		saveBytes( Path.of( "C:\\temp\\intertwine", byteCodeType.typeDescriptor().typeName + ".class" ), byteCodeType.write() );
		System.out.println( ByteCodePrinter.printByteCodeType( byteCodeType, Optional.empty() ) );

		return factory.byteCodeClassLoader.load( byteCodeType );
	}

	private static void addUntwinerInitMethod( ByteCodeType byteCodeType, ByteCodeType interfaceByteCodeType, ByteCodeField exitPointField )
	{
		ByteCodeMethod byteCodeMethod = ByteCodeMethod.of( //
			ByteCodeMethod.modifierEnum.of( ByteCodeMethod.Modifier.Public ), //
			MethodPrototype.of( "<init>", //
				MethodDescriptor.of( TypeDescriptor.of( void.class ), interfaceByteCodeType.typeDescriptor() ) ) );
		byteCodeType.methods.add( byteCodeMethod );
		CodeAttribute code = CodeAttribute.of( 2, 2 );
		byteCodeMethod.attributeSet.addAttribute( code );
		code.ALOAD( 0 );
		code.INVOKESPECIAL( MethodReference.of( MethodReferenceKind.Plain, Object.class, //
			MethodPrototype.of( "<init>", MethodDescriptor.of( void.class ) ) ) );
		code.ALOAD( 0 );
		code.ALOAD( 1 );
		code.PUTFIELD( FieldReference.of( byteCodeType.typeDescriptor(), exitPointField.prototype() ) );
		code.RETURN();
	}

	private static void addUntwinerAnyCallMethod( ByteCodeType byteCodeType, ByteCodeType interfaceByteCodeType, ByteCodeField exitPointField )
	{
		ByteCodeMethod byteCodeMethod = ByteCodeMethod.of( //
			ByteCodeMethod.modifierEnum.of( ByteCodeMethod.Modifier.Public ), //
			MethodPrototype.of( "anyCall", //
				MethodDescriptor.of( TypeDescriptor.of( Object.class ), TypeDescriptor.of( MethodKey.class ), TypeDescriptor.of( Object[].class ) ) ) );
		byteCodeType.methods.add( byteCodeMethod );
		CodeAttribute code = CodeAttribute.of( 4, 4 );
		byteCodeMethod.attributeSet.addAttribute( code );

		StackMapTableAttribute stackMapTableAttribute = StackMapTableAttribute.of();
		code.attributeSet.addAttribute( stackMapTableAttribute );

		code.ALOAD( 1 );
		code.CHECKCAST( TypeDescriptor.of( CompilingKey.class ) );
		code.ASTORE( 3 );
		code.ALOAD( 3 );
		code.GETFIELD( FieldReference.of( CompilingKey.class, FieldPrototype.of( "index", int.class ) ) );
		TableSwitchInstruction tableSwitchInstruction = code.TABLESWITCH( 0 );

		for( int methodIndex = 0; methodIndex < interfaceByteCodeType.methods.size(); methodIndex++ )
		{
			ByteCodeMethod method = interfaceByteCodeType.methods.get( methodIndex );
			if( method.modifiers.contains( ByteCodeMethod.Modifier.Static ) )
				continue;
			if( !method.modifiers.contains( ByteCodeMethod.Modifier.Abstract ) )
				continue;
			assert method.modifiers.contains( ByteCodeMethod.Modifier.Public );
			assert !method.modifiers.contains( ByteCodeMethod.Modifier.Final );
			assert !method.modifiers.contains( ByteCodeMethod.Modifier.Native );
			assert !method.modifiers.contains( ByteCodeMethod.Modifier.Synchronized ); // ?
			assert !method.modifiers.contains( ByteCodeMethod.Modifier.Synthetic ); // ?
			assert !method.modifiers.contains( ByteCodeMethod.Modifier.Bridge ); // ?
			Instruction instruction = addUntwinerSwitchCase( byteCodeType, code, interfaceByteCodeType, method );
			tableSwitchInstruction.targetInstructions.add( instruction );

			if( methodIndex == 0 )
				stackMapTableAttribute.addAppendFrame( instruction, ObjectVerificationType.of( TypeDescriptor.of( CompilingKey.class ) ) );
			else
				stackMapTableAttribute.addSameFrame( instruction );
		}

		Instruction defaultInstruction = code.NEW( TypeDescriptor.of( AssertionError.class ) );
		tableSwitchInstruction.setDefaultInstruction( defaultInstruction );
		stackMapTableAttribute.addSameFrame( defaultInstruction );
		code.DUP();
		code.ALOAD( 3 );
		code.INVOKESPECIAL( MethodReference.of( MethodReferenceKind.Plain, AssertionError.class, MethodPrototype.of( "<init>", MethodDescriptor.of( void.class, Object.class ) ) ) );
		code.ATHROW();
	}

	private static Instruction addUntwinerSwitchCase( ByteCodeType byteCodeType, CodeAttribute code, ByteCodeType interfaceByteCodeType, ByteCodeMethod method )
	{
		Instruction firstInstruction = code.ALOAD( 0 );
		code.GETFIELD( FieldReference.of( byteCodeType.typeDescriptor(), FieldPrototype.of( "exitPoint", FieldDescriptor.of( interfaceByteCodeType.typeDescriptor() ) ) ) );
		List<TypeDescriptor> parameterTypes = method.prototype().descriptor.parameterTypeDescriptors;
		for( int parameterIndex = 0; parameterIndex < parameterTypes.size(); parameterIndex++ )
		{
			TypeDescriptor parameterType = parameterTypes.get( parameterIndex );
			code.ALOAD( 2 );
			code.LDC( parameterIndex );
			code.AALOAD();
			if( parameterType.isPrimitive() )
			{
				PrimitiveTypeInfo primitiveTypeInfo = Kit.map.get( primitiveTypeInfosByTypeDescriptor, parameterType );
				code.CHECKCAST( TypeDescriptor.of( primitiveTypeInfo.wrapperType ) );
				code.INVOKEVIRTUAL( primitiveTypeInfo.unboxingMethod );
			}
			else
			{
				code.CHECKCAST( parameterType );
			}
		}
		code.INVOKEINTERFACE( MethodReference.of( MethodReferenceKind.Interface, interfaceByteCodeType.typeDescriptor(), method.prototype() ), 1 + parameterTypes.size() );
		TypeDescriptor returnType = method.prototype().descriptor.returnTypeDescriptor;
		if( returnType == TypeDescriptor.of( void.class ) )
		{
			code.ACONST_NULL();
			code.ARETURN();
		}
		else if( returnType.isPrimitive() )
		{
			PrimitiveTypeInfo primitiveTypeInfo = Kit.map.get( primitiveTypeInfosByTypeDescriptor, returnType );
			code.INVOKEVIRTUAL( primitiveTypeInfo.unboxingMethod );
			primitiveTypeInfo.returnInstructionFactory.invoke( code );
		}
		else
		{
			code.ARETURN();
		}
		return firstInstruction;
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

	private static final MethodReference integerBoxingMethod = MethodReference.of( MethodReferenceKind.Plain, Integer.class, MethodPrototype.of( "valueOf", MethodDescriptor.of( Integer.class, int.class ) ) );
	private static final MethodReference integerUnboxingMethod = MethodReference.of( MethodReferenceKind.Plain, Integer.class, MethodPrototype.of( "intValue", MethodDescriptor.of( int.class ) ) );

	private static final MethodReference floatBoxingMethod = MethodReference.of( MethodReferenceKind.Plain, Float.class, MethodPrototype.of( "valueOf", MethodDescriptor.of( Float.class, float.class ) ) );
	private static final MethodReference floatUnboxingMethod = MethodReference.of( MethodReferenceKind.Plain, Float.class, MethodPrototype.of( "floatValue", MethodDescriptor.of( float.class ) ) );

	private static final MethodReference longBoxingMethod = MethodReference.of( MethodReferenceKind.Plain, Integer.class, MethodPrototype.of( "valueOf", MethodDescriptor.of( Long.class, long.class ) ) );
	private static final MethodReference longUnboxingMethod = MethodReference.of( MethodReferenceKind.Plain, Integer.class, MethodPrototype.of( "longValue", MethodDescriptor.of( long.class ) ) );

	private static final MethodReference doubleBoxingMethod = MethodReference.of( MethodReferenceKind.Plain, Integer.class, MethodPrototype.of( "valueOf", MethodDescriptor.of( Double.class, double.class ) ) );
	private static final MethodReference doubleUnboxingMethod = MethodReference.of( MethodReferenceKind.Plain, Integer.class, MethodPrototype.of( "doubleValue", MethodDescriptor.of( double.class ) ) );

	private static final class PrimitiveTypeInfo
	{
		final Class<?> primitiveType;
		final Class<?> wrapperType;
		final MethodReference boxingMethod;
		final MethodReference unboxingMethod;
		final Function2<Instruction,CodeAttribute,Integer> loadInstructionFactory;
		final Function1<Instruction,CodeAttribute> returnInstructionFactory;

		PrimitiveTypeInfo( Class<?> primitiveType, Class<?> wrapperType, MethodReference boxingMethod, MethodReference unboxingMethod, //
			Function2<Instruction,CodeAttribute,Integer> loadInstructionFactory, //
			Function1<Instruction,CodeAttribute> returnInstructionFactory )
		{
			this.primitiveType = primitiveType;
			this.wrapperType = wrapperType;
			this.boxingMethod = boxingMethod;
			this.unboxingMethod = unboxingMethod;
			this.loadInstructionFactory = loadInstructionFactory;
			this.returnInstructionFactory = returnInstructionFactory;
		}
	}

	private static final PrimitiveTypeInfo booleanPrimitiveTypeInfo = new PrimitiveTypeInfo( boolean.class, Boolean.class, integerBoxingMethod, integerUnboxingMethod, CodeAttribute::ILOAD, CodeAttribute::IRETURN );
	private static final PrimitiveTypeInfo bytePrimitiveTypeInfo = new PrimitiveTypeInfo( byte.class, Byte.class, integerBoxingMethod, integerUnboxingMethod, CodeAttribute::ILOAD, CodeAttribute::IRETURN );
	private static final PrimitiveTypeInfo charPrimitiveTypeInfo = new PrimitiveTypeInfo( char.class, Character.class, integerBoxingMethod, integerUnboxingMethod, CodeAttribute::ILOAD, CodeAttribute::IRETURN );
	private static final PrimitiveTypeInfo doublePrimitiveTypeInfo = new PrimitiveTypeInfo( double.class, Double.class, doubleBoxingMethod, doubleUnboxingMethod, CodeAttribute::DLOAD, CodeAttribute::DRETURN );
	private static final PrimitiveTypeInfo floatPrimitiveTypeInfo = new PrimitiveTypeInfo( float.class, Float.class, floatBoxingMethod, floatUnboxingMethod, CodeAttribute::FLOAD, CodeAttribute::FRETURN );
	private static final PrimitiveTypeInfo intPrimitiveTypeInfo = new PrimitiveTypeInfo( int.class, Integer.class, integerBoxingMethod, integerUnboxingMethod, CodeAttribute::ILOAD, CodeAttribute::IRETURN );
	private static final PrimitiveTypeInfo longPrimitiveTypeInfo = new PrimitiveTypeInfo( long.class, Long.class, longBoxingMethod, longUnboxingMethod, CodeAttribute::LLOAD, CodeAttribute::LRETURN );
	private static final PrimitiveTypeInfo shortPrimitiveTypeInfo = new PrimitiveTypeInfo( short.class, Short.class, integerBoxingMethod, integerUnboxingMethod, CodeAttribute::ILOAD, CodeAttribute::IRETURN );

	private static final List<PrimitiveTypeInfo> primitiveTypeInfos = List.of( booleanPrimitiveTypeInfo, bytePrimitiveTypeInfo, charPrimitiveTypeInfo, //
		doublePrimitiveTypeInfo, floatPrimitiveTypeInfo, intPrimitiveTypeInfo, longPrimitiveTypeInfo, shortPrimitiveTypeInfo );

	private static final Map<TypeDescriptor,PrimitiveTypeInfo> primitiveTypeInfosByTypeDescriptor = primitiveTypeInfos.stream().collect( Collectors.toMap( //
		p -> TypeDescriptor.of( p.primitiveType ), p -> p ) );
}
