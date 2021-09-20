package mikenakis.bytecode.model.attributes.target;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

public final class FormalParameterTarget extends Target // "formal_parameter_target" in jvms-4.7.20.1
{
	public final int formalParameterIndex;

	public FormalParameterTarget( Type type, int formalParameterIndex )
	{
		super( type );
		assert type == Type.TypeInFormalParameterDeclarationOfMethodConstructorOrLambdaExpression;
		this.formalParameterIndex = formalParameterIndex;
	}

	@Deprecated @Override public Optional<FormalParameterTarget> tryAsFormalParameterTarget() { return Optional.of( this ); }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "formalParameterIndex = " + formalParameterIndex;
	}
}
