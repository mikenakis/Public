package mikenakis.immutability.type.exceptions;

import mikenakis.immutability.helpers.Helpers;
import mikenakis.immutability.mykit.UncheckedException;
import mikenakis.immutability.type.ImmutabilitySelfAssessable;

/**
 * Thrown if the {@link ImmutabilitySelfAssessable} annotation is placed on an interface.
 *
 * @author michael.gr
 */
public class SelfAssessableAnnotationIsOnlyApplicableToClassException extends UncheckedException
{
	public final Class<?> jvmClass;

	public SelfAssessableAnnotationIsOnlyApplicableToClassException( Class<?> jvmClass )
	{
		assert !Helpers.isClass( jvmClass );
		this.jvmClass = jvmClass;
	}
}
