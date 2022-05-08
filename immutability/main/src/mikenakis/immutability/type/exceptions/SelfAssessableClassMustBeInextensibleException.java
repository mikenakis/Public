package mikenakis.immutability.type.exceptions;

import mikenakis.immutability.internal.helpers.Helpers;
import mikenakis.immutability.internal.mykit.UncheckedException;
import mikenakis.immutability.ImmutabilitySelfAssessable;

/**
 * Thrown when a class implements {@link ImmutabilitySelfAssessable} but the class is extensible.
 *
 * @author michael.gr
 */
public class SelfAssessableClassMustBeInextensibleException extends UncheckedException
{
	public final Class<?> jvmClass;

	public SelfAssessableClassMustBeInextensibleException( Class<?> jvmClass )
	{
		assert Helpers.isExtensible( jvmClass );
		this.jvmClass = jvmClass;
	}
}
