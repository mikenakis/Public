package io.github.mikenakis.testana.structure;

import io.github.mikenakis.bytecode.dependencies.ByteCodeDependencies;
import io.github.mikenakis.bytecode.model.ByteCodeMethod;
import io.github.mikenakis.bytecode.model.ByteCodeType;
import io.github.mikenakis.bytecode.model.attributes.CodeAttribute;
import io.github.mikenakis.bytecode.model.attributes.KnownAttribute;
import io.github.mikenakis.bytecode.model.attributes.LineNumberTableAttribute;
import io.github.mikenakis.bytecode.model.descriptors.MethodPrototype;
import io.github.mikenakis.java_type_model.TerminalTypeDescriptor;
import io.github.mikenakis.kit.Kit;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

final class ByteCodeInfo
{
	private static final boolean includeDescriptorOnlyDependencies = Kit.get( false ); //I believe that descriptor-only dependencies are unnecessary.
	private static final boolean includeSignatureDependencies = Kit.get( false ); //I believe that signature dependencies are unnecessary.

	private final ByteCodeType byteCodeType;

	ByteCodeInfo( ByteCodeType byteCodeType )
	{
		this.byteCodeType = byteCodeType;
	}

	Collection<String> getDependencyNames()
	{
		Collection<TerminalTypeDescriptor> dependencies = ByteCodeDependencies.getDependencies( byteCodeType, includeDescriptorOnlyDependencies, includeSignatureDependencies );
		return dependencies.stream().map( d -> d.typeName ).toList();
	}

	String getClassSourceLocation()
	{
		Optional<String> sourceFileName = byteCodeType.tryGetSourceFileName();
		return byteCodeType.typeDescriptor().typeName + "(" + (sourceFileName.orElse( "?" )) + ":" + 1 + ")";
	}

	static String getMethodSourceLocation( String className, MethodPrototype methodPrototype, Function<String,ByteCodeType> byteCodeTypeByName )
	{
		ByteCodeType byteCodeType = byteCodeTypeByName.apply( className );
		ByteCodeMethod byteCodeMethod = byteCodeType.getMethod( methodPrototype, byteCodeTypeByName ).orElseThrow();
		Optional<CodeAttribute> codeAttribute = byteCodeMethod.attributeSet.tryGetKnownAttributeByTag( KnownAttribute.tag_Code ) //
			.map( KnownAttribute::asCodeAttribute );
		Optional<LineNumberTableAttribute> lineNumberTableAttribute = codeAttribute //
			.flatMap( a -> a.attributeSet.tryGetKnownAttributeByTag( KnownAttribute.tag_LineNumberTable ) ) //
			.map( KnownAttribute::asLineNumberTableAttribute );
		int lineNumber = lineNumberTableAttribute.map( a -> a.lineNumberTableEntries.get( 0 ).lineNumber ).orElse( 0 );
		String methodName = byteCodeMethod.name();
		Optional<String> sourceFileName = byteCodeType.tryGetSourceFileName();
		return className + '.' + methodName + "(" + (sourceFileName.orElse( "?" )) + ":" + lineNumber + ")";
	}

	int getDeclaredMethodIndex( MethodPrototype methodPrototype )
	{
		return byteCodeType.findDeclaredMethod( methodPrototype );
	}
}
