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
import io.github.mikenakis.java_type_model.TerminalTypeDescriptor;
import io.github.mikenakis.java_type_model.TypeDescriptor;
import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.kit.functional.Function1;
import io.github.mikenakis.kit.functional.Function2;
import io.github.mikenakis.kit.logging.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
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
	private static final String dummySourceFileName = DummySourceFile.class.getSimpleName() + ".java";

	private final Class<T> interfaceType;
	private final boolean implementDefaultMethods;
	private final TerminalTypeDescriptor interfaceTypeDescriptor;
	private final List<Method> interfaceMethods;
	private final CompilingIntertwineMethodKey<T>[] keys;
	private final Map<Method,CompilingIntertwineMethodKey<T>> keysByMethod;
	private Optional<Constructor<T>> entwinerConstructor = Optional.empty();
	private Optional<Constructor<Anycall<T>>> untwinerConstructor = Optional.empty();

	CompilingIntertwine( Class<T> interfaceType, boolean implementDefaultMethods )
	{
		assert interfaceType.isInterface();
		assert Modifier.isPublic( interfaceType.getModifiers() ) : new IllegalAccessException();
		this.interfaceType = interfaceType;
		this.implementDefaultMethods = implementDefaultMethods;
		ByteCodeType interfaceByteCodeType = ByteCodeType.read( interfaceType );
		interfaceTypeDescriptor = interfaceByteCodeType.typeDescriptor();
		interfaceMethods = getInterfaceMethods( interfaceType, implementDefaultMethods );
		keys = buildArrayOfKey( interfaceMethods );
		keysByMethod = Stream.of( keys ).collect( Collectors.toMap( k -> k.method, k -> k ) );
	}

	private static List<Method> getInterfaceMethods( Class<?> interfaceType, boolean implementDefaultMethods )
	{
		Set<Method> methods = new LinkedHashSet<>();
		getInterfaceMethodsRecursively( interfaceType, methods, implementDefaultMethods );
		return methods.stream().toList();
	}

	private static void getInterfaceMethodsRecursively( Class<?> interfaceType, Set<Method> methods, boolean implementDefaultMethods )
	{
		for( Class<?> superInterfaceType : interfaceType.getInterfaces() )
			getInterfaceMethodsRecursively( superInterfaceType, methods, implementDefaultMethods );
		Arrays.stream( interfaceType.getMethods() ).filter( method -> isInterfaceMethod( method, implementDefaultMethods ) ).forEach( p -> Kit.collection.addOrReplace( methods, p ) );
	}

	private static boolean isInterfaceMethod( Method method, boolean implementDefaultMethods )
	{
		if( Modifier.isStatic( method.getModifiers() ) )
			return false; //skip static methods
		assert Modifier.isPublic( method.getModifiers() );
		assert !Modifier.isFinal( method.getModifiers() );
		assert !Modifier.isNative( method.getModifiers() );
		assert !Modifier.isSynchronized( method.getModifiers() );
		assert !method.isSynthetic();
		assert !method.isBridge();
		if( !Modifier.isAbstract( method.getModifiers() ) ) //if this is a default method
			return implementDefaultMethods;
		return true;
	}

	private CompilingIntertwineMethodKey<T>[] buildArrayOfKey( List<Method> methods )
	{
//		int[] index = new int[1];
//		return methods.stream().map( method -> new CompilingKey<>( this, method, index[0]++ ) ).toList();
		// PEARL: IntellijIdea blooper: IntellijIdea thinks that the following code only needs the "unchecked" suppression.
		// Javac thinks differently.
		// In order to keep Javac happy, the raw-types suppression must be added,
		// but then IntellijIdea thinks that raw-types is redundant and complains about it,
		// so we need to also suppress the "redundant suppression" inspection of IntellijIdea.
		//noinspection RedundantSuppression
		@SuppressWarnings( { "unchecked", "rawtypes" } ) CompilingIntertwineMethodKey<T>[] result = IntStream.range( 0, methods.size() ) //
			.mapToObj( i -> new CompilingIntertwineMethodKey<>( this, methods.get( i ), i ) ) //
			.toArray( CompilingIntertwineMethodKey[]::new );
		return result;
	}

	@Override public Class<T> interfaceType()
	{
		return interfaceType;
	}

	@Override public boolean implementsDefaultMethods()
	{
		return implementDefaultMethods;
	}

	@Override public List<MethodKey<T>> keys()
	{
		return List.of( keys );
	}

	@Override public MethodKey<T> keyByMethod( Method method )
	{
		return Kit.map.get( keysByMethod, method );
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
		String className = "Entwiner_" + identifierFromTypeName( interfaceType, implementDefaultMethods );
		ByteCodeType entwinerByteCodeType = ByteCodeType.of( //
			ByteCodeType.modifierEnum.of( ByteCodeType.Modifier.Public, ByteCodeType.Modifier.Final, ByteCodeType.Modifier.Super ), //
			TerminalTypeDescriptor.of( getClass().getPackageName() + "." + className ), //
			Optional.of( TerminalTypeDescriptor.of( Entwiner.class ) ), //
			List.of( interfaceTypeDescriptor ) );
		FieldPrototype keysFieldPrototype = FieldPrototype.of( "keys", FieldDescriptor.of( CompilingIntertwineMethodKey[].class ) ); // FieldPrototype.of( Kit.unchecked( () -> Entwiner.class.getField( "keys" ) ) );
		entwinerByteCodeType.fields.add( ByteCodeField.of( ByteCodeField.modifierEnum.of( ByteCodeField.Modifier.Private, ByteCodeField.Modifier.Final ), keysFieldPrototype ) );
		FieldPrototype exitPointFieldPrototype = FieldPrototype.of( "exitPoint", FieldDescriptor.of( Anycall.class ) );
		entwinerByteCodeType.fields.add( ByteCodeField.of( ByteCodeField.modifierEnum.of( ByteCodeField.Modifier.Private, ByteCodeField.Modifier.Final ), exitPointFieldPrototype ) );
		addEntwinerInitMethod( entwinerByteCodeType, keysFieldPrototype, exitPointFieldPrototype );
		int maxArgumentCount = maxArgumentCount( interfaceMethods );
		int methodCount = interfaceMethods.size();
		for( int methodIndex = 0; methodIndex < methodCount; methodIndex++ )
			addEntwinerInterfaceMethod( entwinerByteCodeType, maxArgumentCount, keysFieldPrototype, exitPointFieldPrototype, methodIndex, interfaceMethods.get( methodIndex ) );
		entwinerByteCodeType.attributeSet.addAttribute( SourceFileAttribute.of( dummySourceFileName ) );

		if( Kit.areAssertionsEnabled() && Kit.get( false ) )
			save( className, entwinerByteCodeType );

		Class<T> entwinerClass = ByteCodeClassLoader.load( interfaceType.getClassLoader(), entwinerByteCodeType );
		return Kit.unchecked( () -> entwinerClass.getDeclaredConstructor( CompilingIntertwineMethodKey[].class, Anycall.class ) );
	}

	///TODO perhaps also somehow generate debug information so that IntellijIdea can step into (or through) the entwiner?
	private static void addEntwinerInterfaceMethod( ByteCodeType entwinerByteCodeType, int maxMethodArgumentCount, FieldPrototype keysFieldPrototype, //
		FieldPrototype exitPointFieldPrototype, int interfaceMethodIndex, Method interfaceMethod )
	{
		ByteCodeMethod byteCodeMethod = ByteCodeMethod.of( ByteCodeMethod.modifierEnum.of( ByteCodeMethod.Modifier.Public ), interfaceMethod );
		entwinerByteCodeType.methods.add( byteCodeMethod );
		CodeAttribute code = CodeAttribute.of( 5 + maxMethodArgumentCount, 1 + maxMethodArgumentCount );
		byteCodeMethod.attributeSet.addAttribute( code );

		code.ALOAD( 0 ); //push this
		code.GETFIELD( FieldReference.of( entwinerByteCodeType.typeDescriptor(), exitPointFieldPrototype ) ); //pop this, push this->exitPoint

		code.ALOAD( 0 ); //push this
		code.GETFIELD( FieldReference.of( entwinerByteCodeType.typeDescriptor(), keysFieldPrototype ) ); //pop this, push keys
		code.LDC( interfaceMethodIndex ); //push methodIndex
		code.AALOAD(); //pop methodIndex, pop keys, push keys[methodIndex]

		code.LDC( interfaceMethod.getParameterCount() ); //push method-parameter-count
		code.ANEWARRAY( TerminalTypeDescriptor.of( Object.class ) ); //pop method-parameter-count, push new Object[]

		Parameter[] parameters = interfaceMethod.getParameters();
		for( int parameterIndex = 0; parameterIndex < parameters.length; parameterIndex++ )
		{
			Class<?> parameterType = parameters[parameterIndex].getType();
			code.DUP(); //pop Object[], push Object[], Object[]
			code.LDC( parameterIndex ); //push parameterIndex
			if( parameterType.isPrimitive() )
			{
				PrimitiveTypeInfo primitiveTypeInfo = Kit.map.get( primitiveTypeInfosByType, parameterType );
				primitiveTypeInfo.loadInstructionFactory.invoke( code, 1 + getBogusJvmArgumentIndex( interfaceMethod, parameterIndex ) );
				code.INVOKESTATIC( primitiveTypeInfo.boxingMethod );
			}
			else
				code.ALOAD( 1 + getBogusJvmArgumentIndex( interfaceMethod, parameterIndex ) ); //push parameter (from parameters[parameterIndex])
			code.AASTORE(); //pop parameter, pop parameterIndex, pop Object[], set Object[parameterIndex] = parameter
		}

		code.INVOKEINTERFACE( Anycall.methodReference(), 3 );

		Class<?> returnType = interfaceMethod.getReturnType();
		if( returnType.equals( void.class ) )
		{
			code.POP();
			code.RETURN();
		}
		else if( returnType.isPrimitive() )
		{
			PrimitiveTypeInfo primitiveTypeInfo = Kit.map.get( primitiveTypeInfosByType, returnType );
			code.CHECKCAST( TypeDescriptor.of( primitiveTypeInfo.wrapperType ) );
			code.INVOKEVIRTUAL( primitiveTypeInfo.unboxingMethod );
			primitiveTypeInfo.returnInstructionFactory.invoke( code );
		}
		else
		{
			code.CHECKCAST( TypeDescriptor.of( returnType ) );
			code.ARETURN();
		}

		code.attributeSet.addAttribute( LineNumberTableAttribute.of( List.of( LineNumberTableEntry.of( code.instructions.all().get( 0 ), 1 ) ) ) );
	}

	private static void addEntwinerInitMethod( ByteCodeType byteCodeType, FieldPrototype keysFieldPrototype, FieldPrototype exitPointFieldPrototype )
	{
		ByteCodeMethod byteCodeMethod = ByteCodeMethod.of( //
			ByteCodeMethod.modifierEnum.of( ByteCodeMethod.Modifier.Public ), //
			MethodPrototype.of( "<init>", //
				MethodDescriptor.of( TypeDescriptor.of( void.class ), keysFieldPrototype.descriptor.typeDescriptor, TypeDescriptor.of( Anycall.class ) ) ) );
		byteCodeType.methods.add( byteCodeMethod );
		CodeAttribute code = CodeAttribute.of( 2, 3 );
		byteCodeMethod.attributeSet.addAttribute( code );
		code.ALOAD( 0 );
		code.INVOKESPECIAL( constructorMethodReference( Entwiner.class ) );
		code.ALOAD( 0 );
		code.ALOAD( 1 );
		code.PUTFIELD( FieldReference.of( byteCodeType.typeDescriptor(), keysFieldPrototype ) );
		code.ALOAD( 0 );
		code.ALOAD( 2 );
		code.PUTFIELD( FieldReference.of( byteCodeType.typeDescriptor(), exitPointFieldPrototype ) );
		code.RETURN();
	}

	private Constructor<Anycall<T>> createUntwinerClassAndGetConstructor()
	{
		String className = "Untwiner_" + identifierFromTypeName( interfaceType, implementDefaultMethods );
		ByteCodeField exitPointField = ByteCodeField.of( ByteCodeField.modifierEnum.of( ByteCodeField.Modifier.Private, ByteCodeField.Modifier.Final ), //
			untwinerExitPointFieldPrototype( interfaceTypeDescriptor ) );
		ByteCodeType untwinerByteCodeType = ByteCodeType.of( //
			ByteCodeType.modifierEnum.of( ByteCodeType.Modifier.Public, ByteCodeType.Modifier.Final, ByteCodeType.Modifier.Super ), //
			TerminalTypeDescriptor.of( getClass().getPackageName() + "." + className ), //
			Optional.of( TerminalTypeDescriptor.of( Untwiner.class ) ), //
			List.of( TerminalTypeDescriptor.of( Anycall.class ) ) );
		untwinerByteCodeType.fields.add( exitPointField );
		addUntwinerInitMethod( untwinerByteCodeType, interfaceTypeDescriptor, exitPointField );
		addUntwinerAnycallMethod( untwinerByteCodeType, interfaceTypeDescriptor, interfaceMethods, maxArgumentCount( interfaceMethods ) );
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
		code.INVOKESPECIAL( constructorMethodReference( Untwiner.class ) );
		code.ALOAD( 0 );
		code.ALOAD( 1 );
		code.PUTFIELD( FieldReference.of( untwinerByteCodeType.typeDescriptor(), exitPointField.prototype() ) );
		code.RETURN();
	}

	//TODO perhaps also somehow generate debug information so that IntellijIdea can step into (or through) the untwiner?
	private static void addUntwinerAnycallMethod( ByteCodeType untwinerByteCodeType, TerminalTypeDescriptor interfaceTypeDescriptor, //
		List<Method> methods, int maxArgumentCount )
	{
		ByteCodeMethod byteCodeMethod = ByteCodeMethod.of( ByteCodeMethod.modifierEnum.of( ByteCodeMethod.Modifier.Public ), Anycall.methodPrototype() );
		untwinerByteCodeType.methods.add( byteCodeMethod );
		CodeAttribute code = CodeAttribute.of( 3 + maxArgumentCount, 4 );
		byteCodeMethod.attributeSet.addAttribute( code );

		StackMapTableAttribute stackMapTableAttribute = StackMapTableAttribute.of();
		code.attributeSet.addAttribute( stackMapTableAttribute );

		code.ALOAD( 1 );
		code.CHECKCAST( TypeDescriptor.of( CompilingIntertwineMethodKey.class ) );
		code.ASTORE( 3 );
		code.ALOAD( 3 );
		code.GETFIELD( FieldReference.of( CompilingIntertwineMethodKey.class, FieldPrototype.of( "index", int.class ) ) );
		TableSwitchInstruction tableSwitchInstruction = code.TABLESWITCH( 0 );

		int methodCount = methods.size();
		for( int methodIndex = 0; methodIndex < methodCount; methodIndex++ )
		{
			Instruction instruction = addUntwinerSwitchCase( untwinerByteCodeType, code, interfaceTypeDescriptor, methods.get( methodIndex ) );
			tableSwitchInstruction.targetInstructions.add( instruction );
			if( methodIndex == 0 )
				stackMapTableAttribute.addAppendFrame( instruction, ObjectVerificationType.of( TypeDescriptor.of( CompilingIntertwineMethodKey.class ) ) );
			else
				stackMapTableAttribute.addSameFrame( instruction );
		}

		Instruction defaultInstruction = emitThrowAssertionErrorSequence( code, 3 );
		tableSwitchInstruction.setDefaultInstruction( defaultInstruction );
		stackMapTableAttribute.addSameFrame( defaultInstruction );

		code.attributeSet.addAttribute( LineNumberTableAttribute.of( List.of( LineNumberTableEntry.of( code.instructions.all().get( 0 ), 1 ) ) ) );
	}

	private static Instruction addUntwinerSwitchCase( ByteCodeType untwinerByteCodeType, CodeAttribute code, TerminalTypeDescriptor interfaceTypeDescriptor, //
		Method interfaceMethod )
	{
		Instruction firstInstruction = code.ALOAD( 0 );
		code.GETFIELD( FieldReference.of( untwinerByteCodeType.typeDescriptor(), untwinerExitPointFieldPrototype( interfaceTypeDescriptor ) ) );
		Parameter[] parameterTypes = interfaceMethod.getParameters();
		for( int parameterIndex = 0; parameterIndex < parameterTypes.length; parameterIndex++ )
		{
			Class<?> parameterType = parameterTypes[parameterIndex].getType();
			code.ALOAD( 2 );
			code.LDC( parameterIndex );
			code.AALOAD();
			if( parameterType.isPrimitive() )
			{
				PrimitiveTypeInfo primitiveTypeInfo = Kit.map.get( primitiveTypeInfosByType, parameterType );
				code.CHECKCAST( TypeDescriptor.of( primitiveTypeInfo.wrapperType ) );
				code.INVOKEVIRTUAL( primitiveTypeInfo.unboxingMethod );
			}
			else
			{
				code.CHECKCAST( TypeDescriptor.of( parameterType ) );
			}
		}
		code.INVOKEINTERFACE( MethodReference.of( MethodReferenceKind.Interface, interfaceTypeDescriptor, MethodPrototype.of( interfaceMethod ) ), //
			1 + getBogusJvmArgumentCount( interfaceMethod ) );
		Class<?> returnType = interfaceMethod.getReturnType();
		if( returnType == void.class )
		{
			code.ACONST_NULL();
		}
		else if( returnType.isPrimitive() )
		{
			PrimitiveTypeInfo primitiveTypeInfo = Kit.map.get( primitiveTypeInfosByType, returnType );
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

	private static int getBogusJvmArgumentCount( Method method )
	{
		return getBogusJvmArgumentIndex( method, method.getParameterCount() );
	}

	private static int getBogusJvmArgumentIndex( Method method, int argumentIndex )
	{
		Parameter[] parameters = method.getParameters();
		int count = 0;
		for( int i = 0; i < argumentIndex; i++ )
		{
			Class<?> parameterType = parameters[i].getType();
			count++;
			if( parameterType.isPrimitive() )
			{
				if( parameterType == double.class || parameterType == long.class )
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

	private static String identifierFromTypeName( Class<?> type, boolean implementDefaultMethods )
	{
		return type.getName().replace( ".", "_" ).replace( "$", "_" ) + (implementDefaultMethods? "_with_defaults" : "_without_defaults" );
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

	private static final Map<Class<?>,PrimitiveTypeInfo> primitiveTypeInfosByType = Stream.of( //
		new PrimitiveTypeInfo( boolean.class, Boolean.class, booleanBoxingMethod, booleanUnboxingMethod, CodeAttribute::ILOAD, CodeAttribute::IRETURN ), //
		new PrimitiveTypeInfo( byte.class, Byte.class, byteBoxingMethod, byteUnboxingMethod, CodeAttribute::ILOAD, CodeAttribute::IRETURN ),             //
		new PrimitiveTypeInfo( char.class, Character.class, charBoxingMethod, charUnboxingMethod, CodeAttribute::ILOAD, CodeAttribute::IRETURN ),        //
		new PrimitiveTypeInfo( short.class, Short.class, shortBoxingMethod, shortUnboxingMethod, CodeAttribute::ILOAD, CodeAttribute::IRETURN ),         //
		new PrimitiveTypeInfo( int.class, Integer.class, intBoxingMethod, intUnboxingMethod, CodeAttribute::ILOAD, CodeAttribute::IRETURN ),             //
		new PrimitiveTypeInfo( float.class, Float.class, floatBoxingMethod, floatUnboxingMethod, CodeAttribute::FLOAD, CodeAttribute::FRETURN ),         //
		new PrimitiveTypeInfo( long.class, Long.class, longBoxingMethod, longUnboxingMethod, CodeAttribute::LLOAD, CodeAttribute::LRETURN ),             //
		new PrimitiveTypeInfo( double.class, Double.class, doubleBoxingMethod, doubleUnboxingMethod, CodeAttribute::DLOAD, CodeAttribute::DRETURN )      //
	).collect( Collectors.toMap( p -> p.primitiveType, p -> p ) );

	private static MethodReference constructorMethodReference( Class<?> jvmClass )
	{
		return MethodReference.of( MethodReferenceKind.Plain, jvmClass, MethodPrototype.of( "<init>", MethodDescriptor.of( void.class ) ) );
	}

	private static MethodPrototype untwinerInitMethodPrototype( TerminalTypeDescriptor interfaceTypeDescriptor )
	{
		return MethodPrototype.of( "<init>", MethodDescriptor.of( TypeDescriptor.of( void.class ), interfaceTypeDescriptor ) );
	}

	private static int maxArgumentCount( Iterable<Method> methods )
	{
		int max = 0;
		for( Method method : methods )
			max = Math.max( max, getBogusJvmArgumentCount( method ) );
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
