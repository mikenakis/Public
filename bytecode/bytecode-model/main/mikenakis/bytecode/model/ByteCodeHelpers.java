package mikenakis.bytecode.model;

import mikenakis.bytecode.model.attributes.CodeAttribute;
import mikenakis.bytecode.model.attributes.KnownAttribute;
import mikenakis.bytecode.model.attributes.LineNumberTableAttribute;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.constants.MethodHandleConstant;
import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.bytecode.model.constants.NameAndDescriptorConstant;
import mikenakis.bytecode.model.descriptors.ArrayTypeDescriptor;
import mikenakis.bytecode.model.descriptors.MethodDescriptor;
import mikenakis.bytecode.model.descriptors.MethodHandleDescriptor;
import mikenakis.bytecode.model.descriptors.PrimitiveTypeDescriptor;
import mikenakis.bytecode.model.descriptors.TerminalTypeDescriptor;
import mikenakis.bytecode.model.descriptors.TypeDescriptor;
import mikenakis.kit.Kit;

import java.lang.constant.ClassDesc;
import java.lang.constant.DirectMethodHandleDesc;
import java.lang.constant.MethodHandleDesc;
import java.lang.constant.MethodTypeDesc;
import java.util.ArrayList;
import java.util.List;
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
		return byteCodeType.typeDescriptor().typeName() + "(" + (sourceFileName.orElse( "?" )) + ":" + 1 + ")";
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
		return byteCodeType.typeDescriptor().typeName() + '.' + methodName + "(" + (sourceFileName.orElse( "?" )) + ":" + lineNumber + ")";
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

	public static boolean isValidTerminalTypeName( String typeName )
	{
		if( typeName.isEmpty() ) //NOTE: this will probably fail for a single-letter class name in the global scope (outside any package.)
			return false;
		char c = typeName.charAt( 0 );
		if( c == '[' )
			return false;
		if( typeName.startsWith( "L" ) )
			return false;
		if( typeName.endsWith( ";" ) )
			return false;
		if( typeName.contains( "/" ) )
			return false;
		if( typeName.endsWith( "[]" ) )
			return false;
		return true;
	}

	public static String descriptorStringFromInternalName( String internalName )
	{
		assert isValidInternalName( internalName );
		if( internalName.startsWith( "[" ) )
			return internalName;
		if( !internalName.endsWith( ";" ) )
			return "L" + internalName + ";";
		return internalName;
	}

	public static ClassConstant classConstantFromTerminalTypeDescriptor( TerminalTypeDescriptor typeDescriptor )
	{
		ClassConstant classConstant = new ClassConstant();
		classConstant.setInternalNameOrDescriptorStringConstant( Mutf8Constant.of( typeDescriptor.descriptorString() ) );
		return classConstant;
	}

	public static ClassDesc classDescFromClassConstant( ClassConstant classConstant )
	{
		return ClassDesc.ofDescriptor( classConstant.descriptorString() );
	}

	public static DirectMethodHandleDesc directMethodHandleDescFromMethodHandleConstant( MethodHandleConstant methodHandleConstant )
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
		TypeDescriptor returnTypeDescriptor = TypeDescriptor.ofDescriptorString( methodTypeDesc.returnType().descriptorString() );
		List<ClassDesc> parameterClassDescs = methodTypeDesc.parameterList();
		List<TypeDescriptor> parameterTypeDescriptors = new ArrayList<>( parameterClassDescs.size() );
		for( ClassDesc parameterClassDesc : parameterClassDescs )
		{
			TypeDescriptor parameterTypeDescriptor = TypeDescriptor.ofDescriptorString( parameterClassDesc.descriptorString() );
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
			TypeDescriptor componentType = TypeDescriptor.ofDescriptorString( componentDescriptorString );
			return ArrayTypeDescriptor.of( componentType );
		}
		if( classDesc.isPrimitive() )
		{
			String primitiveDescriptorString = classDesc.descriptorString();
			return PrimitiveTypeDescriptor.ofDescriptorString( primitiveDescriptorString );
		}
		String internalName = internalFromDescriptor( descriptorString );
		return TerminalTypeDescriptor.ofInternalName( internalName );
	}

	private static String internalFromDescriptor( String descriptorString )
	{
		return descriptorString.substring( 1, descriptorString.length() - 1 );
	}

	public static boolean isArrayDescriptorString( String descriptorString )
	{
		return descriptorString.charAt( 0 ) == '[';
		//return ClassDesc.ofDescriptor( descriptorString ).isArray();
	}

	public static String typeNameFromDescriptorString( String descriptorString )
	{
		return typeNameFromClassDesc( ClassDesc.ofDescriptor( descriptorString ) );
	}

	public static String descriptorStringFromMethodHandleConstantInvocation( MethodHandleConstant methodHandleConstant )
	{
		return directMethodHandleDescFromMethodHandleConstant( methodHandleConstant ).invocationType().descriptorString();
	}

	public static String descriptorStringFromMethodHandleConstantOwner( MethodHandleConstant methodHandleConstant )
	{
		return directMethodHandleDescFromMethodHandleConstant( methodHandleConstant ).owner().descriptorString();
	}

	public static MethodHandleDescriptor methodHandleDescriptorFromMethodHandleConstant( MethodHandleConstant methodHandleConstant )
	{
		DirectMethodHandleDesc directMethodHandleDesc = directMethodHandleDescFromMethodHandleConstant( methodHandleConstant );
		MethodDescriptor methodDescriptor = MethodDescriptor.ofDescriptorString( directMethodHandleDesc.invocationType().descriptorString() );
		TypeDescriptor ownerTypeDescriptor = TypeDescriptor.ofDescriptorString( directMethodHandleDesc.owner().descriptorString() );
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

	public static String binaryFromInternal( String name )
	{
		return name.replace( '/', '.' );
	}

	public static String descriptorStringFromTypeName( String typeName )
	{
		if( typeName.equals( "boolean" ) )
			return "Z";
		if( typeName.equals( "byte" ) )
			return "B";
		if( typeName.equals( "char" ) )
			return "C";
		if( typeName.equals( "double" ) )
			return "D";
		if( typeName.equals( "float" ) )
			return "F";
		if( typeName.equals( "integer" ) )
			return "I";
		if( typeName.equals( "long" ) )
			return "J";
		if( typeName.equals( "short" ) )
			return "S";
		if( typeName.equals( "void" ) )
			return "V";
		return "L" + typeName.replace( '.', '/' ) + ";";
	}

	public static boolean isValidPrimitiveDescriptorString( String descriptorString )
	{
		if( descriptorString.isEmpty() )
			return false;
		if( descriptorString.length() > 1 )
			return false;
		char c = descriptorString.charAt( 0 );
		return switch( c )
			{
				case 'Z', 'B', 'C', 'D', 'F', 'I', 'J', 'S', 'V' -> true;
				default -> false;
			};
	}
}
