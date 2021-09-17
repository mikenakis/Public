package mikenakis.bytecode.model.attributes.target;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

public final class TypeParameterTarget extends Target // "type_parameter_target" in jvms-4.7.20.1
{
	public final int typeParameterIndex;

	public TypeParameterTarget( int typeParameterIndex )
	{
		this.typeParameterIndex = typeParameterIndex;
	}

	@Deprecated @Override public Optional<TypeParameterTarget> tryAsTypeParameterTarget() { return Optional.of( this ); }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "typeParameterIndex = " + typeParameterIndex;
	}
}
