package mikenakis.bytecode.model.attributes.target;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

public final class TypeParameterBoundTarget extends Target // "type_parameter_bound_target" in jvms-4.7.20.1
{
	public final int typeParameterIndex;
	public final int boundIndex;

	public TypeParameterBoundTarget( int typeParameterIndex, int boundIndex )
	{
		this.typeParameterIndex = typeParameterIndex;
		this.boundIndex = boundIndex;
	}

	@Deprecated @Override public Optional<TypeParameterBoundTarget> tryAsTypeParameterBoundTarget() { return Optional.of( this ); }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "typeParameterIndex = " + typeParameterIndex + ", boundIndex = " + boundIndex;
	}
}