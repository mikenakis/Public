package mikenakis.bytecode.model.attributes.target;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class FormalParameterTarget extends Target // "formal_parameter_target" in jvms-4.7.20.1
{
	public final int formalParameterIndex;

	public FormalParameterTarget( int tag, int formalParameterIndex )
	{
		super( tag );
		assert tag == tagTypeInFormalParameterDeclarationOfMethodConstructorOrLambdaExpression;
		this.formalParameterIndex = formalParameterIndex;
	}

	@Deprecated @Override public FormalParameterTarget asFormalParameterTarget() { return this; }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "formalParameterIndex = " + formalParameterIndex;
	}
}
