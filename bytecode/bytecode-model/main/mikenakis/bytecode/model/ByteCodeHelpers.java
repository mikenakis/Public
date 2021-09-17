package mikenakis.bytecode.model;

import mikenakis.bytecode.kit.StringParser;
import mikenakis.bytecode.model.attributes.CodeAttribute;
import mikenakis.bytecode.model.attributes.LineNumberTableAttribute;

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
		String typeName = byteCodeType.thisClassConstant.getClassName();
		Optional<String> sourceFileName = byteCodeType.tryGetSourceFileName();
		return typeName + "(" + (sourceFileName.orElse( "?" )) + ":" + 1 + ")";
	}

	public static String getMethodSourceLocation( ByteCodeType byteCodeType, ByteCodeMethod byteCodeMethod )
	{
		Optional<CodeAttribute> codeAttribute = byteCodeMethod.attributeSet.tryGetAttributeByName( CodeAttribute.kind.utf8Name ) //
			.map( a -> a.asCodeAttribute() );
		Optional<LineNumberTableAttribute> lineNumberTableAttribute = codeAttribute //
			.flatMap( a -> a.attributeSet().tryGetAttributeByName( LineNumberTableAttribute.kind.utf8Name ) ) //
			.map( a -> a.asLineNumberTableAttribute() );
		int lineNumber = lineNumberTableAttribute.map( a -> a.lineNumbers().get( 0 ).lineNumber() ).orElse( 0 );
		String typeName = byteCodeType.thisClassConstant.getClassName();
		String methodName = byteCodeMethod.nameConstant.value();
		Optional<String> sourceFileName = byteCodeType.tryGetSourceFileName();
		return typeName + '.' + methodName + "(" + (sourceFileName.orElse( "?" )) + ":" + lineNumber + ")";
	}

	static String parseNextJavaTypeNameFromDescriptor( StringParser stringParser )
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
