package mikenakis.immutability;

import mikenakis.immutability.internal.helpers.Stringizer;

/**
 * Base class for all immutability assessments.
 */
public abstract class ImmutabilityAssessment extends Assessment
{
	protected ImmutabilityAssessment( Stringizer stringizer )
	{
		super( stringizer );
	}
}
