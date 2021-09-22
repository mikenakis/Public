package mikenakis.bytecode.model;

import mikenakis.bytecode.model.attributes.CodeAttribute;
import mikenakis.bytecode.model.attributes.KnownAttribute;
import mikenakis.bytecode.model.attributes.LineNumberTableAttribute;

import java.lang.constant.ClassDesc;
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
		String typeName = typeNameFromClassDesc( byteCodeType.descriptor() );
		Optional<String> sourceFileName = byteCodeType.tryGetSourceFileName();
		return typeName + "(" + (sourceFileName.orElse( "?" )) + ":" + 1 + ")";
	}

	public static String getMethodSourceLocation( ByteCodeType byteCodeType, ByteCodeMethod byteCodeMethod )
	{
		Optional<CodeAttribute> codeAttribute = byteCodeMethod.attributeSet.tryGetKnownAttributeByTag( KnownAttribute.tagCode ) //
			.map( a -> a.asCodeAttribute() );
		Optional<LineNumberTableAttribute> lineNumberTableAttribute = codeAttribute //
			.flatMap( a -> a.attributeSet.tryGetKnownAttributeByTag( KnownAttribute.tagLineNumberTable ) ) //
			.map( a -> a.asLineNumberTableAttribute() );
		int lineNumber = lineNumberTableAttribute.map( a -> a.entrys.get( 0 ).lineNumber() ).orElse( 0 );
		String typeName = typeNameFromClassDesc( byteCodeType.descriptor() );
		String methodName = byteCodeMethod.name();
		Optional<String> sourceFileName = byteCodeType.tryGetSourceFileName();
		return typeName + '.' + methodName + "(" + (sourceFileName.orElse( "?" )) + ":" + lineNumber + ")";
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
		if( typeName.length() <= 1 ) //NOTE: this will probably fail for a single-letter class name in the global scope (outside any package.)
			return false;
		if( typeName.startsWith( "L" ) )
			return false;
		if( typeName.startsWith( "[" ) )
			return false;
		if( typeName.endsWith( ";" ) )
			return false;
		if( typeName.contains( "/" ) )
			return false;
		if( typeName.endsWith( "[]" ) )
			return false;
		return true;
	}
}
