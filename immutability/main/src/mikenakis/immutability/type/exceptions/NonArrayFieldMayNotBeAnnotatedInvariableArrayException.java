package mikenakis.immutability.type.exceptions;

import mikenakis.immutability.internal.mykit.UncheckedException;
import mikenakis.immutability.annotations.InvariableArray;

import java.lang.reflect.Field;

/**
 * Thrown if a non-array field is annotated with the @{@link InvariableArray} annotation.
 * The @{@link InvariableArray} annotation is only valid for array fields.
 *
 * @author michael.gr
 */
public class NonArrayFieldMayNotBeAnnotatedInvariableArrayException extends UncheckedException
{
	public final Field field;

	public NonArrayFieldMayNotBeAnnotatedInvariableArrayException( Field field )
	{
		this.field = field;
	}
}
