package mikenakis.immutability.object.assessments;

import mikenakis.immutability.ImmutabilityAssessment;
import mikenakis.immutability.internal.helpers.Stringizer;

public abstract class ObjectImmutabilityAssessment extends ImmutabilityAssessment
{
	protected ObjectImmutabilityAssessment( Stringizer stringizer )
	{
		super( stringizer );
	}
}
