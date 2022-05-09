package mikenakis.immutability.internal.assessments;

import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Signifies that an object is immutable.
 */
public final class ImmutableObjectAssessment extends ObjectAssessment
{
	public static final ImmutableObjectAssessment instance = new ImmutableObjectAssessment( Stringizer.defaultInstance );

	public ImmutableObjectAssessment( Stringizer stringizer )
	{
		super( stringizer );
	}

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		stringBuilder.append( "immutable" );
	}
}
