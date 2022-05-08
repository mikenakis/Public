package mikenakis.immutability.object.exceptions;

import mikenakis.immutability.internal.mykit.UncheckedException;
import mikenakis.immutability.object.assessments.MutableObjectAssessment;

/**
 * Thrown when an object is expected to be immutable, but it is not.
 *
 * @author michael.gr
 */
public final class ObjectMustBeImmutableException extends UncheckedException
{
	public final MutableObjectAssessment mutableObjectAssessment;

	public ObjectMustBeImmutableException( MutableObjectAssessment mutableObjectAssessment )
	{
		this.mutableObjectAssessment = mutableObjectAssessment;
	}
}
