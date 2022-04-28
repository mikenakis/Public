package mikenakis.immutability.type.exceptions;

import mikenakis.immutability.mykit.UncheckedException;
import mikenakis.immutability.type.annotations.InvariableArray;

import java.lang.reflect.Field;

/**
 * Thrown when a non-private field is annotated with @{@link InvariableArray}.
 * A class is not in a position of making any guarantees about the invariability of a field if the field is not private.
 *
 * @author michael.gr
 */
public class AnnotatedInvariableArrayFieldMustBePrivateException extends UncheckedException
{
	public final Field field;

	public AnnotatedInvariableArrayFieldMustBePrivateException( Field field )
	{
		this.field = field;
	}
}
