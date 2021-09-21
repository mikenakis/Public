package mikenakis.bytecode.model.attributes.target;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class TypeParameterBoundTarget extends Target // "type_parameter_bound_target" in jvms-4.7.20.1
{
	public final int typeParameterIndex;
	public final int boundIndex;

	public TypeParameterBoundTarget( Type type, int typeParameterIndex, int boundIndex )
	{
		super( type );
		assert type == Type.TypeInBoundOfTypeParameterDeclarationOfGenericClassOrInterface || type == Type.TypeInBoundOfTypeParameterDeclarationOfGenericMethodOrConstructor;
		this.typeParameterIndex = typeParameterIndex;
		this.boundIndex = boundIndex;
	}

	@Deprecated @Override public TypeParameterBoundTarget asTypeParameterBoundTarget() { return this; }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "typeParameterIndex = " + typeParameterIndex + ", boundIndex = " + boundIndex;
	}
}
