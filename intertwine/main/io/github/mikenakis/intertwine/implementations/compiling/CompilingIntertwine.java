package io.github.mikenakis.intertwine.implementations.compiling;

import io.github.mikenakis.bytecode.ByteCodeClassLoader;
import io.github.mikenakis.bytecode.model.ByteCodeField;
import io.github.mikenakis.bytecode.model.ByteCodeMethod;
import io.github.mikenakis.bytecode.model.ByteCodeType;
import io.github.mikenakis.bytecode.model.attributes.CodeAttribute;
import io.github.mikenakis.bytecode.model.attributes.LineNumberTableAttribute;
import io.github.mikenakis.bytecode.model.attributes.LineNumberTableEntry;
import io.github.mikenakis.bytecode.model.attributes.SourceFileAttribute;
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
import io.github.mikenakis.java_type_model.FieldDescriptor;
import io.github.mikenakis.java_type_model.MethodDescriptor;
import io.github.mikenakis.java_type_model.PrimitiveTypeDescriptor;
import io.github.mikenakis.java_type_model.TerminalTypeDescriptor;
import io.github.mikenakis.java_type_model.TypeDescriptor;
import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.kit.functional.Function1;
import io.github.mikenakis.kit.functional.Function2;
import io.github.mikenakis.kit.logging.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Compiling {@link Intertwine}.
 *
 * @author michael.gr
 */
class CompilingIntertwine<T> implements Intertwine<T>
{
	private static final String dummySourceFileName = "DummySourceFile.java";

	private final Class<? super T> interfaceType;
	private final TerminalTypeDescriptor interfaceTypeDescriptor;
	private final List<MethodPrototype> interfaceMethodPrototypes;
	private final CompilingKey<T>[] keys;
	private final Map<MethodPrototype,CompilingKey<T>> keysByPrototype;
	private Optional<Constructor<T>> entwinerConstructor = Optional.empty();
	private Optional<Constructor<Anycall<T>>> untwinerConstructor = Optional.empty();

	CompilingIntertwine( Class<? super T> interfaceType )
	{
		assert interfaceType.isInterface();
		assert Modifier.isPublic( interfaceType.getModifiers() ) : new IllegalAccessException();
		//this.classLoader = classLoader;
		this.interfaceType = interfaceType;
		ByteCodeType interfaceByteCodeType = ByteCodeType.read( interfaceType );
		interfaceTypeDescriptor = interfaceByteCodeType.typeDescriptor();
		interfaceMethodPrototypes = getInterfaceMethodPrototypes( interfaceType.getClassLoader(), interfaceByteCodeType );
		keys = buildArrayOfKey( interfaceMethodPrototypes );
		keysByPrototype = Stream.of( keys ).collect( Collectors.toMap( k -> k.methodPrototype, k -> k ) );
	}

	private static List<MethodPrototype> getInterfaceMethodPrototypes( ClassLoader classLoader, ByteCodeType interfaceByteCodeType )
	{
		Set<MethodPrototype> methodPrototypes = new LinkedHashSet<>();
		getInterfaceMethodPrototypes( classLoader, interfaceByteCodeType, methodPrototypes );
		return methodPrototypes.stream().toList();
	}

	private static void getInterfaceMethodPrototypes( ClassLoader classLoader, ByteCodeType interfaceByteCodeType, Set<MethodPrototype> methodPrototypes )
	{
		interfaceByteCodeType.interfaces().forEach( superInterfaceTypeDescriptor -> //
		{
			Class<?> superInterfaceType = Kit.unchecked( () -> classLoader.loadClass( superInterfaceTypeDescriptor.typeName ) );
			ByteCodeType superInterfaceByteCodeType = ByteCodeType.read( superInterfaceType );
			getInterfaceMethodPrototypes( classLoader, superInterfaceByteCodeType, methodPrototypes );
		} );
		interfaceByteCodeType.methods.stream().filter( CompilingIntertwine::isInterfaceMethod ).map( ByteCodeMethod::prototype ).forEach( p -> Kit.collection.addOrReplace( methodPrototypes, p ) );
	}

	private static boolean isInterfaceMethod( ByteCodeMethod interfaceMethod )
	{
		if( interfaceMethod.modifiers.contains( ByteCodeMethod.Modifier.Static ) )
			return false; //skip static methods
		if( !interfaceMethod.modifiers.contains( ByteCodeMethod.Modifier.Abstract ) )
			return false; //skip default methods
		assert interfaceMethod.modifiers.contains( ByteCodeMethod.Modifier.Public );
		assert !interfaceMethod.modifiers.contains( ByteCodeMethod.Modifier.Final );
		assert !interfaceMethod.modifiers.contains( ByteCodeMethod.Modifier.Native );
		assert !interfaceMethod.modifiers.contains( ByteCodeMethod.Modifier.Synchronized ); // ?
		assert !interfaceMethod.modifiers.contains( ByteCodeMethod.Modifier.Synthetic ); // ?
		assert !interfaceMethod.modifiers.contains( ByteCodeMethod.Modifier.Bridge ); // ?
		return true;
	}

	private CompilingKey<T>[] buildArrayOfKey( List<MethodPrototype> methodPrototypes )
	{
		// PEARL: IntellijIdea blooper: IntellijIdea thinks that the following code only needs the "unchecked" suppression.
		// Javac thinks differently.
		// In order to keep Javac happy, the raw-types suppression must be added,
		// but then IntellijIdea thinks that raw-types is redundant and complains about it,
		// so we need to also suppress the "redundant suppression" inspection of IntellijIdea.
		//noinspection RedundantSuppression
		@SuppressWarnings( { "unchecked", "rawtypes" } ) CompilingKey<T>[] result = IntStream.range( 0, methodPrototypes.size() ) //
			.mapToObj( i -> new CompilingKey<>( this, methodPrototypes.get( i ), i ) ) //
			.toArray( CompilingKey[]::new );
		return result;
	}

	@Override public Class<? super T> interfaceType()
	{
		return interfaceType;
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

	@Override public T newEntwiner( Anycall<T> exitPoint )
	{
		entwinerConstructor = entwinerConstructor.or( () -> Optional.of( createEntwinerClassAndGetConstructor() ) );
		return Kit.unchecked( () -> entwinerConstructor.orElseThrow().newInstance( keys, exitPoint ) );
	}

	@Override public Anycall<T> newUntwiner( T exitPoint )
	{
		untwinerConstructor = untwinerConstructor.or( () -> Optional.of( createUntwinerClassAndGetConstructor() ) );
		return Kit.unchecked( () -> untwinerConstructor.orElseThrow().newInstance( exitPoint ) );
	}

	private Constructor<T> createEntwinerClassAndGetConstructor()
	{
		String className = "Entwiner_" + identifierFromTypeName( interfaceType );
		ByteCodeType entwinerByteCodeType = ByteCodeType.of( //
			ByteCodeType.modifierEnum.of( ByteCodeType.Modifier.Public, ByteCodeType.Modifier.Final, ByteCodeType.Modifier.Super ), //
			TerminalTypeDescriptor.of( getClass().getPackageName() + "." + className ), //
			Optional.of( TerminalTypeDescriptor.of( Object.class ) ), //
			List.of( interfaceTypeDescriptor ) );
		ByteCodeField keysField = ByteCodeField.of( ByteCodeField.modifierEnum.of( ByteCodeField.Modifier.Private, ByteCodeField.Modifier.Final ), //
			FieldPrototype.of( "keys", FieldDescriptor.of( CompilingKey[].class ) ) );
		entwinerByteCodeType.fields.add( keysField );
		ByteCodeField exitPointField = ByteCodeField.of( ByteCodeField.modifierEnum.of( ByteCodeField.Modifier.Private, ByteCodeField.Modifier.Final ), //
			FieldPrototype.of( "exitPoint", FieldDescriptor.of( Anycall.class ) ) );
		entwinerByteCodeType.fields.add( exitPointField );
		addEntwinerInitMethod( entwinerByteCodeType, keysField, exitPointField );
		int maxArgumentCount = maxArgumentCount( interfaceMethodPrototypes );
		int methodCount = interfaceMethodPrototypes.size();
		for( int methodIndex = 0; methodIndex < methodCount; methodIndex++ )
			addEntwinerInterfaceMethod( entwinerByteCodeType, maxArgumentCount, keysField, exitPointField, methodIndex, interfaceMethodPrototypes.get( methodIndex ) );
		entwinerByteCodeType.attributeSet.addAttribute( SourceFileAttribute.of( dummySourceFileName ) );

		if( Kit.areAssertionsEnabled() && Kit.get( false ) )
			save( className, entwinerByteCodeType );

		Class<T> entwinerClass = ByteCodeClassLoader.load( interfaceType.getClassLoader(), entwinerByteCodeType );
		return Kit.unchecked( () -> entwinerClass.getDeclaredConstructor( CompilingKey[].class, Anycall.class ) );
	}

	///TODO perhaps also somehow generate debug information so that IntellijIdea can step into (or through) the entwiner?
	private static void addEntwinerInterfaceMethod( ByteCodeType entwinerByteCodeType, int maxMethodArgumentCount, ByteCodeField keysField, //
		ByteCodeField exitPointField, int interfaceMethodIndex, MethodPrototype interfaceMethodPrototype )
	{
		ByteCodeMethod byteCodeMethod = ByteCodeMethod.of( ByteCodeMethod.modifierEnum.of( ByteCodeMethod.Modifier.Public ), interfaceMethodPrototype );
		entwinerByteCodeType.methods.add( byteCodeMethod );
		CodeAttribute code = CodeAttribute.of( 5 + maxMethodArgumentCount, 1 + maxMethodArgumentCount );
		byteCodeMethod.attributeSet.addAttribute( code );

		code.ALOAD( 0 ); //push this
		code.GETFIELD( FieldReference.of( entwinerByteCodeType.typeDescriptor(), exitPointField.prototype() ) ); //pop this, push this->exitPoint

		code.ALOAD( 0 ); //push this
		code.GETFIELD( FieldReference.of( entwinerByteCodeType.typeDescriptor(), keysField.prototype() ) ); //pop this, push keys
		code.LDC( interfaceMethodIndex ); //push methodIndex
		code.AALOAD(); //pop methodIndex, pop keys, push keys[methodIndex]

		code.LDC( interfaceMethodPrototype.parameterCount() ); //push method-parameter-count
		code.ANEWARRAY( TerminalTypeDescriptor.of( Object.class ) ); //pop method-parameter-count, push new Object[]

		for( int parameterIndex = 0; parameterIndex < interfaceMethodPrototype.parameterCount(); parameterIndex++ )
		{
			TypeDescriptor parameterTypeDescriptor = interfaceMethodPrototype.descriptor.parameterTypeDescriptors.get( parameterIndex );
			code.DUP(); //pop Object[], push Object[], Object[]
			code.LDC( parameterIndex ); //push parameterIndex
			if( parameterTypeDescriptor.isPrimitive() )
			{
				PrimitiveTypeInfo primitiveTypeInfo = Kit.map.get( primitiveTypeInfosByTypeDescriptor, parameterTypeDescriptor );
				primitiveTypeInfo.loadInstructionFactory.invoke( code, 1 + getBogusJvmArgumentIndex( interfaceMethodPrototype, parameterIndex ) );
				code.INVOKESTATIC( primitiveTypeInfo.boxingMethod );
			}
			else
				code.ALOAD( 1 + getBogusJvmArgumentIndex( interfaceMethodPrototype, parameterIndex ) ); //push parameter (from parameters[parameterIndex])
			code.AASTORE(); //pop parameter, pop parameterIndex, pop Object[], set Object[parameterIndex] = parameter
		}

		code.INVOKEINTERFACE( Anycall.methodReference(), 3 );

		TypeDescriptor returnTypeDescriptor = interfaceMethodPrototype.descriptor.returnTypeDescriptor;
		if( returnTypeDescriptor.equals( TypeDescriptor.of( void.class ) ) )
		{
			code.POP();
			code.RETURN();
		}
		else if( returnTypeDescriptor.isPrimitive() )
		{
			PrimitiveTypeInfo primitiveTypeInfo = Kit.map.get( primitiveTypeInfosByTypeDescriptor, returnTypeDescriptor );
			code.CHECKCAST( TypeDescriptor.of( primitiveTypeInfo.wrapperType ) );
			code.INVOKEVIRTUAL( primitiveTypeInfo.unboxingMethod );
			primitiveTypeInfo.returnInstructionFactory.invoke( code );
		}
		else
		{
			code.CHECKCAST( interfaceMethodPrototype.descriptor.returnTypeDescriptor );
			code.ARETURN();
		}

		code.attributeSet.addAttribute( LineNumberTableAttribute.of( List.of( LineNumberTableEntry.of( code.instructions.all().get( 0 ), 1 ) ) ) );
	}

	private static void addEntwinerInitMethod( ByteCodeType byteCodeType, ByteCodeField keysField, ByteCodeField exitPointField )
	{
		ByteCodeMethod byteCodeMethod = ByteCodeMethod.of( //
			ByteCodeMethod.modifierEnum.of( ByteCodeMethod.Modifier.Public ), //
			MethodPrototype.of( "<init>", //
				MethodDescriptor.of( TypeDescriptor.of( void.class ), keysField.descriptor().typeDescriptor, TypeDescriptor.of( Anycall.class ) ) ) );
		byteCodeType.methods.add( byteCodeMethod );
		CodeAttribute code = CodeAttribute.of( 2, 3 );
		byteCodeMethod.attributeSet.addAttribute( code );
		code.ALOAD( 0 );
		code.INVOKESPECIAL( objectConstructorMethodReference() );
		code.ALOAD( 0 );
		code.ALOAD( 1 );
		code.PUTFIELD( FieldReference.of( byteCodeType.typeDescriptor(), keysField.prototype() ) );
		code.ALOAD( 0 );
		code.ALOAD( 2 );
		code.PUTFIELD( FieldReference.of( byteCodeType.typeDescriptor(), exitPointField.prototype() ) );
		code.RETURN();
	}

	private Constructor<Anycall<T>> createUntwinerClassAndGetConstructor()
	{
		String className = "Untwiner_" + identifierFromTypeName( interfaceType );
		ByteCodeField exitPointField = ByteCodeField.of( ByteCodeField.modifierEnum.of( ByteCodeField.Modifier.Private, ByteCodeField.Modifier.Final ), //
			untwinerExitPointFieldPrototype( interfaceTypeDescriptor ) );
		ByteCodeType untwinerByteCodeType = ByteCodeType.of( //
			ByteCodeType.modifierEnum.of( ByteCodeType.Modifier.Public, ByteCodeType.Modifier.Final, ByteCodeType.Modifier.Super ), //
			TerminalTypeDescriptor.of( getClass().getPackageName() + "." + className ), //
			Optional.of( TerminalTypeDescriptor.of( Object.class ) ), //
			List.of( TerminalTypeDescriptor.of( Anycall.class ) ) );
		untwinerByteCodeType.fields.add( exitPointField );
		addUntwinerInitMethod( untwinerByteCodeType, interfaceTypeDescriptor, exitPointField );
		addUntwinerAnycallMethod( untwinerByteCodeType, interfaceTypeDescriptor, interfaceMethodPrototypes, maxArgumentCount( interfaceMethodPrototypes ) );
		untwinerByteCodeType.attributeSet.addAttribute( SourceFileAttribute.of( dummySourceFileName ) );

		if( Kit.areAssertionsEnabled() && Kit.get( false ) )
			save( className, untwinerByteCodeType );

		Class<T> untwinerClass = ByteCodeClassLoader.load( interfaceType.getClassLoader(), untwinerByteCodeType );
		@SuppressWarnings( "unchecked" ) Constructor<Anycall<T>> result = (Constructor<Anycall<T>>)Kit.unchecked( () -> untwinerClass.getDeclaredConstructor( interfaceType ) );
		return result;
	}

	private static void addUntwinerInitMethod( ByteCodeType untwinerByteCodeType, TerminalTypeDescriptor interfaceTypeDescriptor, ByteCodeField exitPointField )
	{
		ByteCodeMethod byteCodeMethod = ByteCodeMethod.of( ByteCodeMethod.modifierEnum.of( ByteCodeMethod.Modifier.Public ), //
			untwinerInitMethodPrototype( interfaceTypeDescriptor ) );
		untwinerByteCodeType.methods.add( byteCodeMethod );
		CodeAttribute code = CodeAttribute.of( 2, 2 );
		byteCodeMethod.attributeSet.addAttribute( code );
		code.ALOAD( 0 );
		code.INVOKESPECIAL( objectConstructorMethodReference() );
		code.ALOAD( 0 );
		code.ALOAD( 1 );
		code.PUTFIELD( FieldReference.of( untwinerByteCodeType.typeDescriptor(), exitPointField.prototype() ) );
		code.RETURN();
	}

	//TODO perhaps also somehow generate debug information so that IntellijIdea can step into (or through) the untwiner?
	private static void addUntwinerAnycallMethod( ByteCodeType untwinerByteCodeType, TerminalTypeDescriptor interfaceTypeDescriptor, //
		List<MethodPrototype> methodPrototypes, int maxArgumentCount )
	{
		ByteCodeMethod byteCodeMethod = ByteCodeMethod.of( ByteCodeMethod.modifierEnum.of( ByteCodeMethod.Modifier.Public ), Anycall.methodPrototype() );
		untwinerByteCodeType.methods.add( byteCodeMethod );
		CodeAttribute code = CodeAttribute.of( 3 + maxArgumentCount, 4 );
		byteCodeMethod.attributeSet.addAttribute( code );

		StackMapTableAttribute stackMapTableAttribute = StackMapTableAttribute.of();
		code.attributeSet.addAttribute( stackMapTableAttribute );

		code.ALOAD( 1 );
		code.CHECKCAST( TypeDescriptor.of( CompilingKey.class ) );
		code.ASTORE( 3 );
		code.ALOAD( 3 );
		code.GETFIELD( FieldReference.of( CompilingKey.class, FieldPrototype.of( "index", int.class ) ) );
		TableSwitchInstruction tableSwitchInstruction = code.TABLESWITCH( 0 );

		int methodCount = methodPrototypes.size();
		for( int methodIndex = 0; methodIndex < methodCount; methodIndex++ )
		{
			Instruction instruction = addUntwinerSwitchCase( untwinerByteCodeType, code, interfaceTypeDescriptor, methodPrototypes.get( methodIndex ) );
			tableSwitchInstruction.targetInstructions.add( instruction );
			if( methodIndex == 0 )
				stackMapTableAttribute.addAppendFrame( instruction, ObjectVerificationType.of( TypeDescriptor.of( CompilingKey.class ) ) );
			else
				stackMapTableAttribute.addSameFrame( instruction );
		}

		Instruction defaultInstruction = emitThrowAssertionErrorSequence( code, 3 );
		tableSwitchInstruction.setDefaultInstruction( defaultInstruction );
		stackMapTableAttribute.addSameFrame( defaultInstruction );

		code.attributeSet.addAttribute( LineNumberTableAttribute.of( List.of( LineNumberTableEntry.of( code.instructions.all().get( 0 ), 1 ) ) ) );
	}

	private static Instruction addUntwinerSwitchCase( ByteCodeType untwinerByteCodeType, CodeAttribute code, //
		TerminalTypeDescriptor interfaceTypeDescriptor, MethodPrototype interfaceMethodPrototype )
	{
		Instruction firstInstruction = code.ALOAD( 0 );
		code.GETFIELD( FieldReference.of( untwinerByteCodeType.typeDescriptor(), untwinerExitPointFieldPrototype( interfaceTypeDescriptor ) ) );
		List<TypeDescriptor> parameterTypeDescriptors = interfaceMethodPrototype.descriptor.parameterTypeDescriptors;
		for( int parameterIndex = 0; parameterIndex < parameterTypeDescriptors.size(); parameterIndex++ )
		{
			TypeDescriptor parameterTypeDescriptor = parameterTypeDescriptors.get( parameterIndex );
			code.ALOAD( 2 );
			code.LDC( parameterIndex );
			code.AALOAD();
			if( parameterTypeDescriptor.isPrimitive() )
			{
				PrimitiveTypeInfo primitiveTypeInfo = Kit.map.get( primitiveTypeInfosByTypeDescriptor, parameterTypeDescriptor );
				code.CHECKCAST( TypeDescriptor.of( primitiveTypeInfo.wrapperType ) );
				code.INVOKEVIRTUAL( primitiveTypeInfo.unboxingMethod );
			}
			else
			{
				code.CHECKCAST( parameterTypeDescriptor );
			}
		}
		code.INVOKEINTERFACE( MethodReference.of( MethodReferenceKind.Interface, interfaceTypeDescriptor, interfaceMethodPrototype ), //
			1 + getBogusJvmArgumentCount( interfaceMethodPrototype ) );
		TypeDescriptor returnType = interfaceMethodPrototype.descriptor.returnTypeDescriptor;
		if( returnType == TypeDescriptor.of( void.class ) )
		{
			code.ACONST_NULL();
		}
		else if( returnType.isPrimitive() )
		{
			PrimitiveTypeInfo primitiveTypeInfo = Kit.map.get( primitiveTypeInfosByTypeDescriptor, returnType );
			code.INVOKESTATIC( primitiveTypeInfo.boxingMethod );
		}
		else
		{
			//nothing to do; the result is ready on the stack.
		}
		code.ARETURN();
		return firstInstruction;
	}

	private static FieldPrototype untwinerExitPointFieldPrototype( TerminalTypeDescriptor interfaceTypeDescriptor )
	{
		return FieldPrototype.of( "exitPoint", FieldDescriptor.of( interfaceTypeDescriptor ) );
	}

	private static int getBogusJvmArgumentCount( MethodPrototype methodPrototype )
	{
		return getBogusJvmArgumentIndex( methodPrototype, methodPrototype.parameterCount() );
	}

	private static int getBogusJvmArgumentIndex( MethodPrototype methodPrototype, int argumentIndex )
	{
		int count = 0;
		for( int i = 0; i < argumentIndex; i++ )
		{
			TypeDescriptor parameterTypeDescriptor = methodPrototype.descriptor.parameterTypeDescriptors.get( i );
			count++;
			if( parameterTypeDescriptor.isPrimitive() )
			{
				Class<?> jvmClass = parameterTypeDescriptor.asPrimitiveTypeDescriptor().jvmClass;
				if( jvmClass == double.class || jvmClass == long.class )
					count++;
			}
		}
		return count;
	}

	private void save( String className, ByteCodeType entwinerByteCodeType )
	{
		URL url = getClass().getResource( "" );
		assert url != null;
		assert url.getProtocol().equals( "file" );
		var path = Paths.get( Kit.unchecked( () -> url.toURI() ) ).resolve( className + ".class" );
		//var path = Path.of( "C:\\temp\\intertwine", entwinerByteCodeType.typeDescriptor().typeName + ".class" );
		saveBytes( path, entwinerByteCodeType.write() );
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

	private static final MethodReference byteBoxingMethod = MethodReference.of( MethodReferenceKind.Plain, Byte.class, MethodPrototype.of( "valueOf", MethodDescriptor.of( Byte.class, byte.class ) ) );
	private static final MethodReference byteUnboxingMethod = MethodReference.of( MethodReferenceKind.Plain, Byte.class, MethodPrototype.of( "byteValue", MethodDescriptor.of( byte.class ) ) );

	private static final MethodReference booleanBoxingMethod = MethodReference.of( MethodReferenceKind.Plain, Boolean.class, MethodPrototype.of( "valueOf", MethodDescriptor.of( Boolean.class, boolean.class ) ) );
	private static final MethodReference booleanUnboxingMethod = MethodReference.of( MethodReferenceKind.Plain, Boolean.class, MethodPrototype.of( "booleanValue", MethodDescriptor.of( boolean.class ) ) );

	private static final MethodReference shortBoxingMethod = MethodReference.of( MethodReferenceKind.Plain, Short.class, MethodPrototype.of( "valueOf", MethodDescriptor.of( Short.class, short.class ) ) );
	private static final MethodReference shortUnboxingMethod = MethodReference.of( MethodReferenceKind.Plain, Short.class, MethodPrototype.of( "shortValue", MethodDescriptor.of( short.class ) ) );

	private static final MethodReference charBoxingMethod = MethodReference.of( MethodReferenceKind.Plain, Character.class, MethodPrototype.of( "valueOf", MethodDescriptor.of( Character.class, char.class ) ) );
	private static final MethodReference charUnboxingMethod = MethodReference.of( MethodReferenceKind.Plain, Character.class, MethodPrototype.of( "charValue", MethodDescriptor.of( char.class ) ) );

	private static final MethodReference intBoxingMethod = MethodReference.of( MethodReferenceKind.Plain, Integer.class, MethodPrototype.of( "valueOf", MethodDescriptor.of( Integer.class, int.class ) ) );
	private static final MethodReference intUnboxingMethod = MethodReference.of( MethodReferenceKind.Plain, Integer.class, MethodPrototype.of( "intValue", MethodDescriptor.of( int.class ) ) );

	private static final MethodReference floatBoxingMethod = MethodReference.of( MethodReferenceKind.Plain, Float.class, MethodPrototype.of( "valueOf", MethodDescriptor.of( Float.class, float.class ) ) );
	private static final MethodReference floatUnboxingMethod = MethodReference.of( MethodReferenceKind.Plain, Float.class, MethodPrototype.of( "floatValue", MethodDescriptor.of( float.class ) ) );

	private static final MethodReference longBoxingMethod = MethodReference.of( MethodReferenceKind.Plain, Long.class, MethodPrototype.of( "valueOf", MethodDescriptor.of( Long.class, long.class ) ) );
	private static final MethodReference longUnboxingMethod = MethodReference.of( MethodReferenceKind.Plain, Long.class, MethodPrototype.of( "longValue", MethodDescriptor.of( long.class ) ) );

	private static final MethodReference doubleBoxingMethod = MethodReference.of( MethodReferenceKind.Plain, Double.class, MethodPrototype.of( "valueOf", MethodDescriptor.of( Double.class, double.class ) ) );
	private static final MethodReference doubleUnboxingMethod = MethodReference.of( MethodReferenceKind.Plain, Double.class, MethodPrototype.of( "doubleValue", MethodDescriptor.of( double.class ) ) );

	private static final class PrimitiveTypeInfo
	{
		final Class<?> primitiveType;
		final Class<?> wrapperType;
		final MethodReference boxingMethod;
		final MethodReference unboxingMethod;
		final Function2<Instruction,CodeAttribute,Integer> loadInstructionFactory;
		final Function1<Instruction,CodeAttribute> returnInstructionFactory;

		PrimitiveTypeInfo( Class<?> primitiveType, Class<?> wrapperType, MethodReference boxingMethod, MethodReference unboxingMethod, //
			Function2<Instruction,CodeAttribute,Integer> loadInstructionFactory, Function1<Instruction,CodeAttribute> returnInstructionFactory )
		{
			this.primitiveType = primitiveType;
			this.wrapperType = wrapperType;
			this.boxingMethod = boxingMethod;
			this.unboxingMethod = unboxingMethod;
			this.loadInstructionFactory = loadInstructionFactory;
			this.returnInstructionFactory = returnInstructionFactory;
		}
	}

	private static final Map<TypeDescriptor,PrimitiveTypeInfo> primitiveTypeInfosByTypeDescriptor = Stream.of( //
		new PrimitiveTypeInfo( boolean.class, Boolean.class, booleanBoxingMethod, booleanUnboxingMethod, CodeAttribute::ILOAD, CodeAttribute::IRETURN ), //
		new PrimitiveTypeInfo( byte.class, Byte.class, byteBoxingMethod, byteUnboxingMethod, CodeAttribute::ILOAD, CodeAttribute::IRETURN ),             //
		new PrimitiveTypeInfo( char.class, Character.class, charBoxingMethod, charUnboxingMethod, CodeAttribute::ILOAD, CodeAttribute::IRETURN ),        //
		new PrimitiveTypeInfo( short.class, Short.class, shortBoxingMethod, shortUnboxingMethod, CodeAttribute::ILOAD, CodeAttribute::IRETURN ),         //
		new PrimitiveTypeInfo( int.class, Integer.class, intBoxingMethod, intUnboxingMethod, CodeAttribute::ILOAD, CodeAttribute::IRETURN ),             //
		new PrimitiveTypeInfo( float.class, Float.class, floatBoxingMethod, floatUnboxingMethod, CodeAttribute::FLOAD, CodeAttribute::FRETURN ),         //
		new PrimitiveTypeInfo( long.class, Long.class, longBoxingMethod, longUnboxingMethod, CodeAttribute::LLOAD, CodeAttribute::LRETURN ),             //
		new PrimitiveTypeInfo( double.class, Double.class, doubleBoxingMethod, doubleUnboxingMethod, CodeAttribute::DLOAD, CodeAttribute::DRETURN )      //
	).collect( Collectors.toMap( p -> PrimitiveTypeDescriptor.of( p.primitiveType ), p -> p ) );

	private static MethodReference objectConstructorMethodReference()
	{
		return MethodReference.of( MethodReferenceKind.Plain, Object.class, MethodPrototype.of( "<init>", MethodDescriptor.of( void.class ) ) );
	}

	private static MethodPrototype untwinerInitMethodPrototype( TerminalTypeDescriptor interfaceTypeDescriptor )
	{
		return MethodPrototype.of( "<init>", MethodDescriptor.of( TypeDescriptor.of( void.class ), interfaceTypeDescriptor ) );
	}

	private static int maxArgumentCount( Iterable<MethodPrototype> methodPrototypes )
	{
		int max = 0;
		for( MethodPrototype methodPrototype : methodPrototypes )
			max = Math.max( max, getBogusJvmArgumentCount( methodPrototype ) );
		return max;
	}

	private static Instruction emitThrowAssertionErrorSequence( CodeAttribute code, int localVariableIndex )
	{
		Instruction result = code.NEW( TypeDescriptor.of( AssertionError.class ) );
		code.DUP();
		code.ALOAD( localVariableIndex );
		code.INVOKESPECIAL( MethodReference.of( MethodReferenceKind.Plain, AssertionError.class, MethodPrototype.of( "<init>", MethodDescriptor.of( void.class, Object.class ) ) ) );
		code.ATHROW();
		return result;
	}
}
