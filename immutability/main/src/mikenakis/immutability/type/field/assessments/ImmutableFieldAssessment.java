package mikenakis.immutability.type.field.assessments;

import mikenakis.immutability.helpers.Stringizer;
import mikenakis.immutability.mykit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Signifies that a field is immutable.
 */
public final class ImmutableFieldAssessment extends FieldAssessment
{
	public ImmutableFieldAssessment( Stringizer stringizer ) { super( stringizer ); }

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		stringBuilder.append( "immutable" );
	}
}
