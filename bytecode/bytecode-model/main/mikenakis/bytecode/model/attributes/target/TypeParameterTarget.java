package mikenakis.bytecode.model.attributes.target;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class TypeParameterTarget extends Target // "type_parameter_target" in jvms-4.7.20.1
{
	public final int typeParameterIndex;

	public TypeParameterTarget( Type type, int typeParameterIndex )
	{
		super( type );
		assert type == Type.TypeParameterDeclarationOfGenericClassOrInterface || type == Type.TypeParameterDeclarationOfGenericMethodOrConstructor;
		this.typeParameterIndex = typeParameterIndex;
	}

	@Deprecated @Override public TypeParameterTarget asTypeParameterTarget() { return this; }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "typeParameterIndex = " + typeParameterIndex;
	}
}
