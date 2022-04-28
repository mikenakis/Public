package mikenakis.immutability.object.exceptions;

import mikenakis.immutability.Assessment;
import mikenakis.immutability.mykit.MyKit;
import mikenakis.immutability.mykit.UncheckedException;
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

	public String fullAssessmentText()
	{
		StringBuilder stringBuilder = new StringBuilder();
		MyKit.<Assessment>tree( mutableObjectAssessment, a -> a.children(), a -> a.toString(), s -> stringBuilder.append( "    " ).append( s ).append( "\r\n" ) );
		return stringBuilder.toString();
	}
}
