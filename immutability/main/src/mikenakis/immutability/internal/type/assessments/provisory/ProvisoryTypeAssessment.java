package mikenakis.immutability.internal.type.assessments.provisory;

import mikenakis.immutability.internal.type.assessments.TypeAssessment;

/**
 * Signifies that a class cannot be conclusively classified as either mutable or immutable, so further runtime checks are necessary on instances of this class.
 */
public abstract class ProvisoryTypeAssessment extends TypeAssessment
{
	public final Class<?> type;

	protected ProvisoryTypeAssessment( Class<?> type ) { this.type = type; }
}
