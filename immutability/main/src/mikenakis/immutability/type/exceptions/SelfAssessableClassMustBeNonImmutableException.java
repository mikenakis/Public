package mikenakis.immutability.type.exceptions;

import mikenakis.immutability.internal.helpers.Helpers;
import mikenakis.immutability.internal.mykit.UncheckedException;
import mikenakis.immutability.type.ImmutabilitySelfAssessable;

/**
 * Thrown when a class implements {@link ImmutabilitySelfAssessable} but the class is already immutable.
 *
 * @author michael.gr
 */
public class SelfAssessableClassMustBeNonImmutableException extends UncheckedException
{
	public final Class<?> jvmClass;

	public SelfAssessableClassMustBeNonImmutableException( Class<?> jvmClass )
	{
		assert !Helpers.isExtensible( jvmClass );
		this.jvmClass = jvmClass;
	}
}
