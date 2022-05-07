package mikenakis.immutability.object.exceptions;

import mikenakis.immutability.internal.mykit.UncheckedException;
import mikenakis.immutability.object.assessments.MutableObjectImmutabilityAssessment;

/**
 * Thrown when an object is expected to be immutable, but it is not.
 *
 * @author michael.gr
 */
public final class ObjectMustBeImmutableException extends UncheckedException
{
	public final MutableObjectImmutabilityAssessment mutableObjectAssessment;

	public ObjectMustBeImmutableException( MutableObjectImmutabilityAssessment mutableObjectAssessment )
	{
		this.mutableObjectAssessment = mutableObjectAssessment;
	}
}
