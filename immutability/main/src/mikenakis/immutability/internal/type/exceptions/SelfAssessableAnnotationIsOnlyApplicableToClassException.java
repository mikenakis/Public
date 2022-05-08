package mikenakis.immutability.internal.type.exceptions;

import mikenakis.immutability.internal.helpers.Helpers;
import mikenakis.immutability.internal.mykit.UncheckedException;
import mikenakis.immutability.ImmutabilitySelfAssessable;

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
