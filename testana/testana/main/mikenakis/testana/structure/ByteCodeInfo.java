package mikenakis.testana.structure;

import mikenakis.bytecode.dependencies.ByteCodeDependencies;
import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.printing.ByteCodePrinter;
import mikenakis.java_type_model.MethodDescriptor;
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
		return ByteCodeHelpers.getClassSourceLocation( byteCodeType );
	}

	String getMethodSourceLocation( String methodName, MethodDescriptor methodDescriptor, Function<String,ByteCodeType> byteCodeTypeByName )
	{
		ByteCodeMethod byteCodeMethod = byteCodeType.getMethodByNameAndDescriptor( methodName, methodDescriptor, byteCodeTypeByName ).orElseThrow();
		return ByteCodeHelpers.getMethodSourceLocation( byteCodeType, byteCodeMethod );
	}

	int getDeclaredMethodIndex( String methodName, MethodDescriptor methodDescriptor )
	{
		return byteCodeType.findDeclaredMethodByNameAndDescriptor( methodName, methodDescriptor );
	}
}
