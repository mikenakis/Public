package mikenakis.bytecode.model.attributes.target;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class TypeArgumentTarget extends Target // "type_argument_target" in jvms-4.7.20.1
{
	public final int offset;
	public final int typeArgumentIndex;

	public TypeArgumentTarget( int tag, int offset, int typeArgumentIndex )
	{
		super( tag );
		assert tag == tagTypeInCastExpression || //
			tag == tagTypeArgumentForGenericConstructorInNewExpressionOrExplicitConstructorInvocationStatement || //
			tag == tagTypeArgumentForGenericMethodInMethodInvocationExpression || //
			tag == tagTypeArgumentForGenericConstructorInMethodReferenceExpressionUsingNew || //
			tag == tagTypeArgumentForGenericMethodInMethodReferenceExpressionUsingIdentifier;
		this.offset = offset;
		this.typeArgumentIndex = typeArgumentIndex;
	}

	@Deprecated @Override public TypeArgumentTarget asTypeArgumentTarget() { return this; }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "offset = " + offset + ", typeArgumentIndex = " + typeArgumentIndex;
	}
}
