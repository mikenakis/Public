package mikenakis.immutability.type.assessments.mutable;

import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.type.assessments.MutableTypeAssessment;

/**
 * Signifies that a type is mutable because it is an array.
 */
public final class MutableArrayAssessment extends MutableTypeAssessment
{
	public MutableArrayAssessment( Stringizer stringizer, Class<?> jvmClass )
	{
		super( stringizer, jvmClass );
		assert jvmClass.isArray();
	}

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " because it is an array class" );
	}
}