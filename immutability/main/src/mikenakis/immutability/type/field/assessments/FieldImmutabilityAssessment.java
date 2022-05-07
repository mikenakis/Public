package mikenakis.immutability.type.field.assessments;

import mikenakis.immutability.ImmutabilityAssessment;
import mikenakis.immutability.internal.helpers.Stringizer;

public abstract class FieldImmutabilityAssessment extends ImmutabilityAssessment
{
	protected FieldImmutabilityAssessment( Stringizer stringizer )
	{
		super( stringizer );
	}
}
