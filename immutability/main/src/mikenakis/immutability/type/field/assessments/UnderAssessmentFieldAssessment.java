package mikenakis.immutability.type.field.assessments;

import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;

public final class UnderAssessmentFieldAssessment extends FieldAssessment
{
	public UnderAssessmentFieldAssessment( Stringizer stringizer ) { super( stringizer ); }

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		stringBuilder.append( "under assessment" );
	}
}
