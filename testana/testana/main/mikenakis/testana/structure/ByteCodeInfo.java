package mikenakis.testana.structure;

import mikenakis.bytecode.dependencies.ByteCodeDependencies;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.attributes.CodeAttribute;
import mikenakis.bytecode.model.attributes.KnownAttribute;
import mikenakis.bytecode.model.attributes.LineNumberTableAttribute;
import mikenakis.bytecode.model.descriptors.MethodPrototype;
import mikenakis.bytecode.printing.ByteCodePrinter;
import mikenakis.java_type_model.TerminalTypeDescriptor;
import mikenakis.kit.Kit;

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
		ByteCodePrinter.printByteCodeType( byteCodeType, Optional.empty() ); //TODO this is only here for testing, must be removed!
		Collection<TerminalTypeDescriptor> dependencies = ByteCodeDependencies.getDependencies( byteCodeType, includeDescriptorOnlyDependencies, includeSignatureDependencies );
		return dependencies.stream().map( d -> d.typeName ).toList();
	}

	String getClassSourceLocation()
	{
		Optional<String> sourceFileName = byteCodeType.tryGetSourceFileName();
		return byteCodeType.typeDescriptor().typeName + "(" + (sourceFileName.orElse( "?" )) + ":" + 1 + ")";
	}

	String getMethodSourceLocation( MethodPrototype methodPrototype, Function<String,ByteCodeType> byteCodeTypeByName )
	{
		ByteCodeMethod byteCodeMethod = byteCodeType.getMethod( methodPrototype, byteCodeTypeByName ).orElseThrow();
		Optional<CodeAttribute> codeAttribute = byteCodeMethod.attributeSet.tryGetKnownAttributeByTag( KnownAttribute.tag_Code ) //
			.map( a -> a.asCodeAttribute() );
		Optional<LineNumberTableAttribute> lineNumberTableAttribute = codeAttribute //
			.flatMap( a -> a.attributeSet.tryGetKnownAttributeByTag( KnownAttribute.tag_LineNumberTable ) ) //
			.map( a -> a.asLineNumberTableAttribute() );
		int lineNumber = lineNumberTableAttribute.map( a -> a.entrys.get( 0 ).lineNumber ).orElse( 0 );
		String methodName = byteCodeMethod.name();
		Optional<String> sourceFileName = byteCodeType.tryGetSourceFileName();
		return byteCodeType.typeDescriptor().typeName + '.' + methodName + "(" + (sourceFileName.orElse( "?" )) + ":" + lineNumber + ")";
	}

	int getDeclaredMethodIndex( MethodPrototype methodPrototype )
	{
		return byteCodeType.findDeclaredMethod( methodPrototype );
	}
}
