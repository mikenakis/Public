package mikenakis.immutability.internal.type.assessments;

/**
 * Signifies that a class is mutable.
 */
public abstract class MutableTypeAssessment extends NonImmutableTypeAssessment
{
	protected MutableTypeAssessment( Class<?> type ) { super( type ); }
}
