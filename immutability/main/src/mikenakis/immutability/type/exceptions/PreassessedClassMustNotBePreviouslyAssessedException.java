package mikenakis.immutability.type.exceptions;

import mikenakis.immutability.internal.mykit.UncheckedException;
import mikenakis.immutability.type.assessments.TypeImmutabilityAssessment;

/**
 * Thrown when an attempt is made to preassess a class, but there has already been a request to assess the immutability of this class, and the answer
 * that was given was that the class is mutable.
 * The order of operations should be changed so as to avoid this inconsistency.
 *
 * @author michael.gr
 */
public class PreassessedClassMustNotBePreviouslyAssessedException extends UncheckedException
{
	public final TypeImmutabilityAssessment previousTypeAssessment;

	public PreassessedClassMustNotBePreviouslyAssessedException( TypeImmutabilityAssessment previousTypeAssessment )
	{
		this.previousTypeAssessment = previousTypeAssessment;
	}
}
