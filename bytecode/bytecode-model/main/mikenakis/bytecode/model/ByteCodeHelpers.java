package mikenakis.bytecode.model;

import mikenakis.bytecode.model.attributes.CodeAttribute;
import mikenakis.bytecode.model.attributes.KnownAttribute;
import mikenakis.bytecode.model.attributes.LineNumberTableAttribute;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.constants.MethodHandleConstant;
import mikenakis.bytecode.model.constants.NameAndDescriptorConstant;
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
import java.util.Optional;

/**
 * ByteCode helpers.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ByteCodeHelpers
{
	private ByteCodeHelpers()
	{
	}

	public static String getClassSourceLocation( ByteCodeType byteCodeType )
	{
		Optional<String> sourceFileName = byteCodeType.tryGetSourceFileName();
		return byteCodeType.typeDescriptor().typeName + "(" + (sourceFileName.orElse( "?" )) + ":" + 1 + ")";
	}

	public static String getMethodSourceLocation( ByteCodeType byteCodeType, ByteCodeMethod byteCodeMethod )
	{
		Optional<CodeAttribute> codeAttribute = byteCodeMethod.attributeSet.tryGetKnownAttributeByTag( KnownAttribute.tag_Code ) //
			.map( a -> a.asCodeAttribute() );
		Optional<LineNumberTableAttribute> lineNumberTableAttribute = codeAttribute //
			.flatMap( a -> a.attributeSet.tryGetKnownAttributeByTag( KnownAttribute.tag_LineNumberTable ) ) //
			.map( a -> a.asLineNumberTableAttribute() );
		int lineNumber = lineNumberTableAttribute.map( a -> a.entrys.get( 0 ).lineNumber() ).orElse( 0 );
		String methodName = byteCodeMethod.name();
		Optional<String> sourceFileName = byteCodeType.tryGetSourceFileName();
		return byteCodeType.typeDescriptor().typeName + '.' + methodName + "(" + (sourceFileName.orElse( "?" )) + ":" + lineNumber + ")";
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

	public static ClassConstant classConstantFromTerminalTypeDescriptor( TerminalTypeDescriptor terminalTypeDescriptor )
	{
		return ClassConstant.of( terminalTypeDescriptor );
	}

	private static ClassDesc classDescFromClassConstant( ClassConstant classConstant )
	{
		return ClassDesc.ofDescriptor( descriptorStringFromInternalName( classConstant.getInternalNameOrDescriptorStringConstant().stringValue() ) );
	}

	private static DirectMethodHandleDesc directMethodHandleDescFromMethodHandleConstant( MethodHandleConstant methodHandleConstant )
	{
		NameAndDescriptorConstant nameAndDescriptorConstant = methodHandleConstant.getReferenceConstant().getNameAndDescriptorConstant();
		return MethodHandleDesc.of( //
			DirectMethodHandleDesc.Kind.valueOf( //
				methodHandleConstant.referenceKind().number, //
				methodHandleConstant.referenceKind() == MethodHandleConstant.ReferenceKind.InvokeInterface ), //I do not know why the isInterface parameter is needed here, but it does not work otherwise.
			classDescFromClassConstant( methodHandleConstant.getReferenceConstant().getDeclaringTypeConstant() ), //
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

	public static TypeDescriptor typeDescriptorFromDescriptorString( String descriptorString )
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

	private static String descriptorStringFromMethodHandleConstantInvocation( MethodHandleConstant methodHandleConstant )
	{
		return directMethodHandleDescFromMethodHandleConstant( methodHandleConstant ).invocationType().descriptorString();
	}

	private static String descriptorStringFromMethodHandleConstantOwner( MethodHandleConstant methodHandleConstant )
	{
		return directMethodHandleDescFromMethodHandleConstant( methodHandleConstant ).owner().descriptorString();
	}

	public static TypeDescriptor typeDescriptorFromMethodHandleConstantOwner( MethodHandleConstant methodHandleConstant )
	{
		return typeDescriptorFromDescriptorString( descriptorStringFromMethodHandleConstantOwner( methodHandleConstant ) );
	}

	public static MethodDescriptor methodDescriptorFromMethodHandleConstantInvocation( MethodHandleConstant methodHandleConstant )
	{
		return methodDescriptorFromDescriptorString( descriptorStringFromMethodHandleConstantInvocation( methodHandleConstant ) );
	}

	public static MethodHandleDescriptor methodHandleDescriptorFromMethodHandleConstant( MethodHandleConstant methodHandleConstant )
	{
		DirectMethodHandleDesc directMethodHandleDesc = directMethodHandleDescFromMethodHandleConstant( methodHandleConstant );
		MethodDescriptor methodDescriptor = methodDescriptorFromDescriptorString( directMethodHandleDesc.invocationType().descriptorString() );
		TypeDescriptor ownerTypeDescriptor = typeDescriptorFromDescriptorString( directMethodHandleDesc.owner().descriptorString() );
		return MethodHandleDescriptor.of( methodDescriptor, ownerTypeDescriptor );
	}

//		if( isValidInternalName( nameConstant.stringValue() ) )
//			Log.debug( nameConstant.stringValue() + " : OK"  );
//		else
//			Log.error( nameConstant.stringValue() + " : Not OK"  );

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
		return descriptorStringFromTerminalTypeDescriptor( typeDescriptor.asTerminalTypeDescriptor() );
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

	private static String descriptorStringFromTerminalTypeDescriptor( TerminalTypeDescriptor terminalTypeDescriptor )
	{
		String internalName = internalFromBinary( terminalTypeDescriptor.typeName );
		return "L" + internalName + ";";
	}

	private static PrimitiveTypeDescriptor primitiveTypeDescriptorFromDescriptorString( String descriptorString )
	{
		assert descriptorString.length() == 1;
		char descriptorChar = descriptorString.charAt( 0 );
		return switch( descriptorChar )
			{
				case 'Z' -> PrimitiveTypeDescriptor.Boolean;
				case 'B' -> PrimitiveTypeDescriptor.Byte;
				case 'C' -> PrimitiveTypeDescriptor.Char;
				case 'D' -> PrimitiveTypeDescriptor.Double;
				case 'F' -> PrimitiveTypeDescriptor.Float;
				case 'I' -> PrimitiveTypeDescriptor.Int;
				case 'J' -> PrimitiveTypeDescriptor.Long;
				case 'S' -> PrimitiveTypeDescriptor.Short;
				case 'V' -> PrimitiveTypeDescriptor.Void;
				default -> throw new AssertionError( descriptorChar );
			};
	}

	private static final Map<PrimitiveTypeDescriptor,Character> charByPrimitiveType = Map.of( //
		PrimitiveTypeDescriptor.Boolean /**/, 'Z',  //
		PrimitiveTypeDescriptor.Byte    /**/, 'B',  //
		PrimitiveTypeDescriptor.Char    /**/, 'C',  //
		PrimitiveTypeDescriptor.Double  /**/, 'D',  //
		PrimitiveTypeDescriptor.Float   /**/, 'F',  //
		PrimitiveTypeDescriptor.Int     /**/, 'I',  //
		PrimitiveTypeDescriptor.Long    /**/, 'J',  //
		PrimitiveTypeDescriptor.Short   /**/, 'S',  //
		PrimitiveTypeDescriptor.Void    /**/, 'V' );

	public static String internalNameFromTerminalTypeDescriptor( TerminalTypeDescriptor terminalTypeDescriptor )
	{
		String binaryName = terminalTypeDescriptor.typeName;
		return internalFromBinary( binaryName );
	}

	public static TerminalTypeDescriptor terminalTypeDescriptorFromInternalName( String internalName )
	{
		String binaryName = binaryFromInternal( internalName );
		return TerminalTypeDescriptor.ofTypeName( binaryName );
	}

	public static String descriptorStringFromMethodDescriptor( MethodDescriptor methodDescriptor )
	{
		ClassDesc returnDesc = classDescFromTypeDescriptor( methodDescriptor.returnTypeDescriptor );
		ClassDesc[] paramDescs = new ClassDesc[methodDescriptor.parameterTypeDescriptors.size()];
		for( int i = 0;  i < paramDescs.length;  i++ )
			paramDescs[i] = ClassDesc.ofDescriptor( descriptorStringFromTypeDescriptor( methodDescriptor.parameterTypeDescriptors.get( i ) ) );
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
