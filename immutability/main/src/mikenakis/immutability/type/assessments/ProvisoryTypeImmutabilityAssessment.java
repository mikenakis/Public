package mikenakis.immutability.type.assessments;

import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Signifies that a class cannot be conclusively classified as either mutable or immutable, so further runtime checks are necessary on instances of this class.
 */
public abstract class ProvisoryTypeImmutabilityAssessment extends NonImmutableTypeImmutabilityAssessment
{
	protected ProvisoryTypeImmutabilityAssessment( Stringizer stringizer, Class<?> jvmClass )
	{
		super( stringizer, jvmClass );
	}

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " is provisory" );
	}
}
