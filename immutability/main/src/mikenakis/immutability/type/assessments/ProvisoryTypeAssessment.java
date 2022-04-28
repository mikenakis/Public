package mikenakis.immutability.type.assessments;

import mikenakis.immutability.helpers.Stringizer;
import mikenakis.immutability.mykit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Signifies that a class cannot be conclusively classified as either mutable or immutable, so further runtime checks are necessary on instances of this class.
 */
public abstract class ProvisoryTypeAssessment extends NonImmutableTypeAssessment
{
	protected ProvisoryTypeAssessment( Stringizer stringizer, Class<?> jvmClass )
	{
		super( stringizer, jvmClass );
	}

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " is provisory" );
	}
}
