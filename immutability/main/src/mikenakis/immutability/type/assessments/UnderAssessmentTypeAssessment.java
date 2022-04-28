package mikenakis.immutability.type.assessments;

import mikenakis.immutability.helpers.Stringizer;
import mikenakis.immutability.mykit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Signifies that a type is currently under assessment.
 *
 * This class is used internally while assessing the immutability of types, so as to avoid infinite recursion when a class graph is cyclic,
 * and should never be seen by the user.
 */
public final class UnderAssessmentTypeAssessment extends TypeAssessment
{
	public UnderAssessmentTypeAssessment( Stringizer stringizer ) { super( stringizer ); }

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		stringBuilder.append( "under assessment" );
	}
}
