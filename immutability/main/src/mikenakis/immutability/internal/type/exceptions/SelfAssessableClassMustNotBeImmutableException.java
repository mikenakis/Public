package mikenakis.immutability.internal.type.exceptions;

import mikenakis.immutability.ImmutabilitySelfAssessable;
import mikenakis.immutability.internal.helpers.Helpers;
import mikenakis.immutability.internal.mykit.UncheckedException;

/**
 * Thrown when a class implements {@link ImmutabilitySelfAssessable} but the class is already immutable.
 *
 * @author michael.gr
 */
public class SelfAssessableClassMustNotBeImmutableException extends UncheckedException
{
	public final Class<?> jvmClass;

	public SelfAssessableClassMustNotBeImmutableException( Class<?> jvmClass )
	{
		assert !Helpers.isExtensible( jvmClass );
		this.jvmClass = jvmClass;
	}
}
