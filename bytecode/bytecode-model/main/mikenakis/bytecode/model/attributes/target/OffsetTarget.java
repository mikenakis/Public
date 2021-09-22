package mikenakis.bytecode.model.attributes.target;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class OffsetTarget extends Target
{
	public final int offset;

	public OffsetTarget( int tag, int offset )
	{
		super( tag );
		assert tag == tagTypeInInstanceofExpression || tag == tagTypeInNewExpression || tag == tagTypeInMethodReferenceExpressionUsingNew ||
			tag == tagTypeInMethodReferenceExpressionUsingIdentifier;
		this.offset = offset;
	}

	@Deprecated @Override public OffsetTarget asOffsetTarget() { return this; }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "offset = " + offset;
	}
}
