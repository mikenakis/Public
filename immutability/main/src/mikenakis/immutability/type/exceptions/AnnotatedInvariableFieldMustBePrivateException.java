package mikenakis.immutability.type.exceptions;

import mikenakis.immutability.internal.mykit.UncheckedException;
import mikenakis.immutability.annotations.Invariable;

import java.lang.reflect.Field;

/**
 * Thrown when a non-private field is annotated with @{@link Invariable}.
 * A class is not in a position of making any guarantees about the invariability of a field if the field is not private.
 *
 * @author michael.gr
 */
public class AnnotatedInvariableFieldMustBePrivateException extends UncheckedException
{
	public final Field field;

	public AnnotatedInvariableFieldMustBePrivateException( Field field )
	{
		this.field = field;
	}
}
