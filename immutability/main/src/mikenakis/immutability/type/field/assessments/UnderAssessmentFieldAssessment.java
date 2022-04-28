package mikenakis.immutability.type.field.assessments;

import mikenakis.immutability.helpers.Stringizer;
import mikenakis.immutability.mykit.annotations.ExcludeFromJacocoGeneratedReport;

public final class UnderAssessmentFieldAssessment extends FieldAssessment
{
	public UnderAssessmentFieldAssessment( Stringizer stringizer ) { super( stringizer ); }

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		stringBuilder.append( "under assessment" );
	}
}
