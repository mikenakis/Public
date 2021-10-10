package mikenakis.bytecode.model;

import mikenakis.bytecode.model.constants.MethodHandleConstant;
import mikenakis.bytecode.model.constants.NameAndDescriptorConstant;
import mikenakis.bytecode.model.constants.ReferenceConstant;
import mikenakis.bytecode.model.constants.ReferenceKind;
import mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import mikenakis.bytecode.model.descriptors.MethodHandleDescriptor;
import mikenakis.bytecode.model.descriptors.MethodPrototype;
import mikenakis.java_type_model.ArrayTypeDescriptor;
import mikenakis.java_type_model.MethodDescriptor;
import mikenakis.java_type_model.PrimitiveTypeDescriptor;
import mikenakis.java_type_model.TerminalTypeDescriptor;
import mikenakis.java_type_model.TypeDescriptor;
import mikenakis.kit.Kit;

import java.lang.constant.ClassDesc;
import java.lang.constant.DirectMethodHandleDesc;
import java.lang.constant.MethodHandleDesc;
import java.lang.constant.MethodTypeDesc;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ByteCode helpers.
 *
 * @author michael.gr
 */
public final class ByteCodeHelpers
{
	private ByteCodeHelpers()
	{
	}

	public static String typeNameFromClassDesc( ClassDesc classDesc )
	{
		if( classDesc.isPrimitive() )
			return classDesc.displayName();
		if( classDesc.isClassOrInterface() )
			return classDesc.packageName() + "." + classDesc.displayName();
		assert classDesc.isArray();
		return typeNameFromClassDesc( classDesc.componentType() ) + "[]";
	}

	private static String descriptorStringFromInternalName( String internalName )
	{
		assert isValidInternalName( internalName );
		if( internalName.startsWith( "[" ) )
			return internalName;
		if( !internalName.endsWith( ";" ) )
			return "L" + internalName + ";";
		return internalName;
	}

	public static DirectMethodHandleDesc directMethodHandleDescFromMethodHandleConstant( MethodHandleConstant methodHandleConstant )
	{
		ReferenceConstant referenceConstant = methodHandleConstant.getReferenceConstant();
		NameAndDescriptorConstant nameAndDescriptorConstant = referenceConstant.getNameAndDescriptorConstant();
		return MethodHandleDesc.of( //
			DirectMethodHandleDesc.Kind.valueOf( //
				methodHandleConstant.referenceKind().number, //
				methodHandleConstant.referenceKind() == ReferenceKind.InvokeInterface ), //I do not know why the isInterface parameter is needed here, but it does not work otherwise.
			ClassDesc.ofDescriptor( descriptorStringFromInternalName( referenceConstant.getDeclaringTypeConstant().internalNameOrDescriptorString() ) ), //
			nameAndDescriptorConstant.getNameConstant().stringValue(), //
			nameAndDescriptorConstant.getDescriptorConstant().stringValue() );
	}

	public static MethodDescriptor methodDescriptorFromDescriptorString( String descriptorString )
	{
		MethodTypeDesc methodTypeDesc = MethodTypeDesc.ofDescriptor( descriptorString );
		TypeDescriptor returnTypeDescriptor = typeDescriptorFromDescriptorString( methodTypeDesc.returnType().descriptorString() );
		List<ClassDesc> parameterClassDescs = methodTypeDesc.parameterList();
		List<TypeDescriptor> parameterTypeDescriptors = new ArrayList<>( parameterClassDescs.size() );
		for( ClassDesc parameterClassDesc : parameterClassDescs )
		{
			TypeDescriptor parameterTypeDescriptor = typeDescriptorFromDescriptorString( parameterClassDesc.descriptorString() );
			parameterTypeDescriptors.add( parameterTypeDescriptor );
		}
		return MethodDescriptor.of( returnTypeDescriptor, parameterTypeDescriptors );
	}

	public static TypeDescriptor typeDescriptorFromDescriptorStringConstant( Mutf8ValueConstant descriptorStringConstant )
	{
		return typeDescriptorFromDescriptorString( descriptorStringConstant.stringValue() );
	}

	private static TypeDescriptor typeDescriptorFromDescriptorString( String descriptorString )
	{
		ClassDesc classDesc = ClassDesc.ofDescriptor( descriptorString );
		if( classDesc.isArray() )
		{
			String componentDescriptorString = classDesc.componentType().descriptorString();
			TypeDescriptor componentType = typeDescriptorFromDescriptorString( componentDescriptorString );
			return ArrayTypeDescriptor.ofComponent( componentType );
		}
		if( classDesc.isPrimitive() )
		{
			String primitiveDescriptorString = classDesc.descriptorString();
			return primitiveTypeDescriptorFromDescriptorString( primitiveDescriptorString );
		}
		String internalName = internalFromDescriptor( descriptorString );
		return terminalTypeDescriptorFromInternalName( internalName );
	}

	private static String internalFromDescriptor( String descriptorString )
	{
		assert descriptorString.charAt( 0 ) == 'L';
		assert descriptorString.charAt( descriptorString.length() - 1 ) == ';';
		return descriptorString.substring( 1, descriptorString.length() - 1 );
	}

	public static TypeDescriptor typeDescriptorFromMethodHandleConstantOwner( MethodHandleConstant methodHandleConstant )
	{
		DirectMethodHandleDesc methodHandleDesc = directMethodHandleDescFromMethodHandleConstant( methodHandleConstant );
		String descriptorString = methodHandleDesc.owner().descriptorString();
		return typeDescriptorFromDescriptorString( descriptorString );
	}

	public static MethodDescriptor methodDescriptorFromMethodHandleConstantInvocation( MethodHandleConstant methodHandleConstant )
	{
		DirectMethodHandleDesc methodHandleDesc = directMethodHandleDescFromMethodHandleConstant( methodHandleConstant );
		String descriptorString = methodHandleDesc.invocationType().descriptorString();
		return methodDescriptorFromDescriptorString( descriptorString );
	}

	public static MethodHandleDescriptor methodHandleDescriptorFromMethodHandleConstant( MethodHandleConstant methodHandleConstant )
	{
		DirectMethodHandleDesc directMethodHandleDesc = directMethodHandleDescFromMethodHandleConstant( methodHandleConstant );
		MethodDescriptor methodDescriptor = methodDescriptorFromDescriptorString( directMethodHandleDesc.invocationType().descriptorString() );
		TypeDescriptor ownerTypeDescriptor = typeDescriptorFromDescriptorString( directMethodHandleDesc.owner().descriptorString() );
		return MethodHandleDescriptor.of( methodDescriptor, ownerTypeDescriptor );
	}

	public static boolean isValidInternalName( String name )
	{
		if( name.isEmpty() )
			return false;
		for( String part : Kit.string.splitAtCharacter( name, '/' ) )
			if( !isValidInternalNamePart( part ) )
				return false;
		return true;
	}

	private static boolean isValidInternalNamePart( String part )
	{
		if( part.isEmpty() )
			return false;
		int n = part.length();
		for( int i = 0; i < n; i++ )
		{
			char c = part.charAt( i );
			if( (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '_' || c == '$' )
				continue;
			return false;
		}
		return true;
	}

	public static boolean isValidDescriptorString( String name )
	{
		int n = name.length();
		assert n >= 1;
		char c = name.charAt( 0 );
		int i = 0;
		while( c == '[' )
		{
			i++;
			if( i >= n )
				break;
			c = name.charAt( i );
		}
		switch( c )
		{
			case 'J', 'I', 'C', 'S', 'B', 'D', 'F', 'Z', 'V':
				assert i + 1 == n;
				return true;
			case 'L':
			{
				i++;
				assert i + 1 < n;
				assert name.charAt( n - 1 ) == ';';
				n--;
				for( ; i < n; i++ )
				{
					c = name.charAt( i );
					assert c != '.' && c != ';' && c != '[';
				}
				break;
			}
			default:
				assert false;
		}
		return true;
	}

	public static String internalFromBinary( String name )
	{
		return name.replace( '.', '/' );
	}

	private static String binaryFromInternal( String name )
	{
		return name.replace( '/', '.' );
	}

	public static ArrayTypeDescriptor arrayTypeDescriptorFromDescriptorString( String descriptorString )
	{
		assert isValidDescriptorString( descriptorString );
		ClassDesc classDesc = ClassDesc.ofDescriptor( descriptorString );
		assert classDesc.isArray();
		TypeDescriptor componentType = typeDescriptorFromDescriptorString( classDesc.componentType().descriptorString() );
		return ArrayTypeDescriptor.ofComponent( componentType );
	}

	public static String descriptorStringFromTypeDescriptor( TypeDescriptor typeDescriptor )
	{
		if( typeDescriptor.isPrimitive() )
			return descriptorStringFromPrimitiveTypeDescriptor( typeDescriptor.asPrimitiveTypeDescriptor() );
		if( typeDescriptor.isArray() )
			return descriptorStringFromArrayTypeDescriptor( typeDescriptor.asArrayTypeDescriptor() );
		assert typeDescriptor.isTerminal();
		String internalName = internalFromBinary( typeDescriptor.asTerminalTypeDescriptor().typeName );
		return "L" + internalName + ";";
	}

	public static String descriptorStringFromPrimitiveTypeDescriptor( PrimitiveTypeDescriptor primitiveTypeDescriptor )
	{
		char character = Kit.map.get( charByPrimitiveType, primitiveTypeDescriptor );
		return String.valueOf( character );
	}

	public static String descriptorStringFromArrayTypeDescriptor( ArrayTypeDescriptor arrayTypeDescriptor )
	{
		return "[" + descriptorStringFromTypeDescriptor( arrayTypeDescriptor.componentTypeDescriptor );
	}

	private static PrimitiveTypeDescriptor primitiveTypeDescriptorFromDescriptorString( String descriptorString )
	{
		assert descriptorString.length() == 1;
		char descriptorChar = descriptorString.charAt( 0 );
		return switch( descriptorChar )
			{
				case 'Z' -> PrimitiveTypeDescriptor.of( boolean.class );
				case 'B' -> PrimitiveTypeDescriptor.of( byte.class );
				case 'C' -> PrimitiveTypeDescriptor.of( char.class );
				case 'D' -> PrimitiveTypeDescriptor.of( double.class );
				case 'F' -> PrimitiveTypeDescriptor.of( float.class );
				case 'I' -> PrimitiveTypeDescriptor.of( int.class );
				case 'J' -> PrimitiveTypeDescriptor.of( long.class );
				case 'S' -> PrimitiveTypeDescriptor.of( short.class );
				case 'V' -> PrimitiveTypeDescriptor.of( void.class );
				default -> throw new AssertionError( descriptorChar );
			};
	}

	private static final Map<PrimitiveTypeDescriptor,Character> charByPrimitiveType = Map.of( //
		PrimitiveTypeDescriptor.of( boolean.class ) /**/, 'Z',  //
		PrimitiveTypeDescriptor.of( byte.class )    /**/, 'B',  //
		PrimitiveTypeDescriptor.of( char.class )    /**/, 'C',  //
		PrimitiveTypeDescriptor.of( double.class )  /**/, 'D',  //
		PrimitiveTypeDescriptor.of( float.class )   /**/, 'F',  //
		PrimitiveTypeDescriptor.of( int.class )     /**/, 'I',  //
		PrimitiveTypeDescriptor.of( long.class )    /**/, 'J',  //
		PrimitiveTypeDescriptor.of( short.class )   /**/, 'S',  //
		PrimitiveTypeDescriptor.of( void.class )    /**/, 'V' );

	public static String internalNameFromTerminalTypeDescriptor( TerminalTypeDescriptor terminalTypeDescriptor )
	{
		String binaryName = terminalTypeDescriptor.typeName;
		return internalFromBinary( binaryName );
	}

	public static TerminalTypeDescriptor terminalTypeDescriptorFromInternalName( String internalName )
	{
		String binaryName = binaryFromInternal( internalName );
		return TerminalTypeDescriptor.of( binaryName );
	}

	public static String descriptorStringFromMethodDescriptor( MethodDescriptor methodDescriptor )
	{
		ClassDesc returnDesc = classDescFromTypeDescriptor( methodDescriptor.returnTypeDescriptor );
		ClassDesc[] paramDescs = new ClassDesc[methodDescriptor.parameterTypeDescriptors.size()];
		for( int i = 0; i < paramDescs.length; i++ )
			paramDescs[i] = classDescFromTypeDescriptor( methodDescriptor.parameterTypeDescriptors.get( i ) );
		MethodTypeDesc methodTypeDesc = MethodTypeDesc.of( returnDesc, paramDescs );
		return methodTypeDesc.descriptorString();
	}

	private static ClassDesc classDescFromTypeDescriptor( TypeDescriptor typeDescriptor )
	{
		return ClassDesc.ofDescriptor( descriptorStringFromTypeDescriptor( typeDescriptor ) );
	}

	public static MethodPrototype methodPrototypeFromNameAndDescriptorConstant( NameAndDescriptorConstant nameAndDescriptorConstant )
	{
		String name = nameAndDescriptorConstant.getNameConstant().stringValue();
		MethodDescriptor descriptor = methodDescriptorFromDescriptorString( nameAndDescriptorConstant.getDescriptorConstant().stringValue() );
		return MethodPrototype.of( name, descriptor );
	}

	public static TypeDescriptor typeDescriptorFromInternalNameOrDescriptorString( String internalNameOrDescriptorString )
	{
		if( internalNameOrDescriptorString.charAt( 0 ) == '[' )
			return arrayTypeDescriptorFromDescriptorString( internalNameOrDescriptorString );
		return terminalTypeDescriptorFromInternalName( internalNameOrDescriptorString );
	}
}
