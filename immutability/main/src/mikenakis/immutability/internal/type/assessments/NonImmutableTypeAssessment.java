package mikenakis.immutability.internal.type.assessments;

/**
 * Common base class for all non-immutable type assessments.
 */
public abstract class NonImmutableTypeAssessment extends TypeAssessment
{
	public final Class<?> type;

	protected NonImmutableTypeAssessment( Class<?> type )
	{
		this.type = type;
	}
}
