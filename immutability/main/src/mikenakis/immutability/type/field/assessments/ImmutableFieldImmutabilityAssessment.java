package mikenakis.immutability.type.field.assessments;

import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Signifies that a field is immutable.
 */
public final class ImmutableFieldImmutabilityAssessment extends FieldImmutabilityAssessment
{
	public ImmutableFieldImmutabilityAssessment( Stringizer stringizer ) { super( stringizer ); }

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		stringBuilder.append( "immutable" );
	}
}
