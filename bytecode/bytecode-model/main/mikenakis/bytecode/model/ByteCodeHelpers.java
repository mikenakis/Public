package mikenakis.bytecode.model;

import mikenakis.bytecode.model.attributes.CodeAttribute;
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
		Optional<CodeAttribute> codeAttribute = byteCodeMethod.attributeSet.tryGetAttributeByName( CodeAttribute.kind.mutf8Name ) //
			.map( a -> a.asCodeAttribute() );
		Optional<LineNumberTableAttribute> lineNumberTableAttribute = codeAttribute //
			.flatMap( a -> a.attributeSet().tryGetAttributeByName( LineNumberTableAttribute.kind.mutf8Name ) ) //
			.map( a -> a.asLineNumberTableAttribute() );
		int lineNumber = lineNumberTableAttribute.map( a -> a.lineNumbers().get( 0 ).lineNumber() ).orElse( 0 );
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
		if( classDesc.isArray() )
			return typeNameFromClassDesc( classDesc.componentType() ) + "[]";
		assert false;
		return "";
	}
}
