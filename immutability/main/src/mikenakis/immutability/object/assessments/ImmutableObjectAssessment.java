package mikenakis.immutability.object.assessments;

import mikenakis.immutability.helpers.Stringizer;
import mikenakis.immutability.mykit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Signifies that an object is immutable.
 */
public final class ImmutableObjectAssessment extends ObjectAssessment
{
	public ImmutableObjectAssessment( Stringizer stringizer )
	{
		super( stringizer );
	}

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		stringBuilder.append( "immutable" );
	}
}
