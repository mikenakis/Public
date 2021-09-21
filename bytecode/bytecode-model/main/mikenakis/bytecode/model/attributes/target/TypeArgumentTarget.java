package mikenakis.bytecode.model.attributes.target;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class TypeArgumentTarget extends Target // "type_argument_target" in jvms-4.7.20.1
{
	public final int offset;
	public final int typeArgumentIndex;

	public TypeArgumentTarget( Type type, int offset, int typeArgumentIndex )
	{
		super( type );
		assert type == Type.TypeInCastExpression || type == Type.TypeArgumentForGenericConstructorInNewExpressionOrExplicitConstructorInvocationStatement || type == Type.TypeArgumentForGenericMethodInMethodInvocationExpression || type == Type.TypeArgumentForGenericConstructorInMethodReferenceExpressionUsingNew || type == Type.TypeArgumentForGenericMethodInMethodReferenceExpressionUsingIdentifier;
		this.offset = offset;
		this.typeArgumentIndex = typeArgumentIndex;
	}

	@Deprecated @Override public TypeArgumentTarget asTypeArgumentTarget() { return this; }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "offset = " + offset + ", typeArgumentIndex = " + typeArgumentIndex;
	}
}
