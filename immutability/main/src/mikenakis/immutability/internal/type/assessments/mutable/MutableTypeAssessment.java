package mikenakis.immutability.internal.type.assessments.mutable;

import mikenakis.immutability.internal.type.assessments.NonImmutableTypeAssessment;

/**
 * Signifies that a class is mutable.
 */
public abstract class MutableTypeAssessment extends NonImmutableTypeAssessment
{
	protected MutableTypeAssessment( Class<?> type ) { super( type ); }
}
