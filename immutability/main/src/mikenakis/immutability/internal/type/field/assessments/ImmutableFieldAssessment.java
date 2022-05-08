package mikenakis.immutability.internal.type.field.assessments;

import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;

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
