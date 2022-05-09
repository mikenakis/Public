package mikenakis.immutability.internal.assessments;

/**
 * Signifies that an object is mutable.
 */
public abstract class MutableObjectAssessment extends ObjectAssessment
{
	public final Object object;

	protected MutableObjectAssessment( Object object )
	{
		this.object = object;
	}
}
