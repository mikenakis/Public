package mikenakis.immutability.internal.type.assessments;

/**
 * Signifies that a type is currently under immutability assessment.  Should never be seen by the user.
 * <p>
 * This class is used internally while assessing the immutability of types, so as to avoid infinite recursion when a class graph is cyclic.
 */
public final class UnderAssessmentTypeAssessment extends TypeAssessment
{
	public UnderAssessmentTypeAssessment() { }
}
