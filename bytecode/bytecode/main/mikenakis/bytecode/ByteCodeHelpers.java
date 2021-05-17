package mikenakis.bytecode;

import mikenakis.bytecode.attributes.CodeAttribute;
import mikenakis.bytecode.attributes.LineNumberTableAttribute;
import mikenakis.bytecode.kit.StringParser;

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

	public static String getPackageFromTypeName( String typeName )
	{
		int i = typeName.lastIndexOf( '.' );
		if( i < 0 )
			return "";
		return typeName.substring( 0, i );
	}

	public static String getClassSourceLocation( ByteCodeType byteCodeType )
	{
		String typeName = byteCodeType.getName();
		String sourceFileName = byteCodeType.getSourceFileName();
		return typeName + "(" + sourceFileName + ":" + 1 + ")";
	}

	public static String getMethodSourceLocation( ByteCodeMethod method )
	{
		ByteCodeType type = method.declaringType;
		Optional<CodeAttribute> codeAttribute = CodeAttribute.tryFrom( method.attributes );
		Optional<LineNumberTableAttribute> lineNumberTableAttribute = codeAttribute.isEmpty() ? Optional.empty() : LineNumberTableAttribute.tryFrom( codeAttribute.get().attributes );
		int lineNumber = lineNumberTableAttribute.isEmpty() ? 0 : lineNumberTableAttribute.get().entries.get( 0 ).lineNumber;
		String typeName = type.getName();
		String methodName = method.getName();
		String sourceFileName = type.getSourceFileName();
		return typeName + '.' + methodName + "(" + sourceFileName + ":" + lineNumber + ")";
	}

	//  0   1   2   3 ->
	//  0,  3,  2,  1
	//        =>
	// 00b 01b 10b 11b ->
	// 00b 11b 10b 01b
	// ==  ~=  ==  ~=
	public static int getPadding( int position )
	{
		int result = (((position & 1) << 1) ^ position) & 3;
		//assert result >= 0 && result <= 3;
		assert ((position + result) & 3) == 0;
		return result;
	}

	public static String parseNextJavaTypeNameFromDescriptor( StringParser stringParser )
	{
		char c = stringParser.parseNextCharacter();
		switch( c )
		{
			//@formatter:off
			case 'B': return "byte";
			case 'C': return "char";
			case 'D': return "double";
			case 'F': return "float";
			case 'I': return "int";
			case 'J': return "long";
			case 'S': return "short";
			case 'Z': return "boolean";
			case 'V': return "void";
			//@formatter:on
			case '[':
			{
				String typeName = parseNextJavaTypeNameFromDescriptor( stringParser );
				return typeName + "[]";
			}
			case 'L':
			{
				String jvmTypeName = stringParser.parseUntil( ';' );
				return getJavaTypeNameFromJvmTypeName( jvmTypeName );
			}
			default:
				assert false;
				return null;
		}
	}

	public static String getJavaTypeNameFromDescriptorTypeName( String jvmDescriptor )
	{
		StringParser stringParser = new StringParser( jvmDescriptor, 0, jvmDescriptor.length() );
		String javaTypeName = parseNextJavaTypeNameFromDescriptor( stringParser );
		assert stringParser.isAtEnd();
		return javaTypeName;
	}

	public static String getJavaTypeNameFromJvmTypeName( String jvmTypeName )
	{
		if( jvmTypeName.startsWith( "[" ) )
			return getJavaTypeNameFromDescriptorTypeName( jvmTypeName );
		return jvmTypeName.replace( '/', '.' );
	}

	public static String getConstantId( int constantIndex )
	{
		return "#" + constantIndex;
	}

	public static String getPcId( int pc )
	{
		return "@" + pc;
	}

	public static boolean isJavaTypeName( String typeName )
	{
		if( typeName.length() <= 1 )
			return false;
		if( typeName.startsWith( "L" ) )
			return false;
		if( typeName.startsWith( "[" ) )
			return false;
		if( typeName.endsWith( ";" ) )
			return false;
		if( typeName.contains( "/" ) )
			return false;
//		if( typeName.endsWith( "[]" ) )
//			return false;
		return true;
	}

	public static String getDescriptorTypeNameFromJavaTypeName( String javaTypeName )
	{
		assert isJavaTypeName( javaTypeName );
		if( javaTypeName.endsWith( "[]" ) )
		{
			javaTypeName = javaTypeName.substring( 0, javaTypeName.length() - 2 );
			return '[' + getDescriptorTypeNameFromJavaTypeName( javaTypeName );
		}
		return 'L' + javaTypeName.replace( '.', '/' ) + ";";
	}
}
