package mikenakis.immutability.object.assessments;

import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Signifies that an object is immutable.
 */
public final class ImmutableObjectImmutabilityAssessment extends ObjectImmutabilityAssessment
{
	public ImmutableObjectImmutabilityAssessment( Stringizer stringizer )
	{
		super( stringizer );
	}

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		stringBuilder.append( "immutable" );
	}
}
