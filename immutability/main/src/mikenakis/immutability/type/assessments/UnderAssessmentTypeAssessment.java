package mikenakis.immutability.type.assessments;

import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Signifies that a type is currently under immutability assessment.  Should never be seen by the user.
 *
 * This class is used internally while assessing the immutability of types, so as to avoid infinite recursion when a class graph is cyclic.
 */
public final class UnderAssessmentTypeAssessment extends TypeAssessment
{
	public UnderAssessmentTypeAssessment( Stringizer stringizer ) { super( stringizer ); }

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		stringBuilder.append( "under assessment" );
	}
}
