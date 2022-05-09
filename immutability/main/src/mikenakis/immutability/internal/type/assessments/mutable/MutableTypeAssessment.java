package mikenakis.immutability.internal.type.assessments.mutable;

import mikenakis.immutability.internal.type.assessments.TypeAssessment;

/**
 * Signifies that a class is mutable.
 */
public abstract class MutableTypeAssessment extends TypeAssessment
{
	public final Class<?> type;

	protected MutableTypeAssessment( Class<?> type ) { this.type = type; }
}
