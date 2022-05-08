package mikenakis.immutability.internal.type.exceptions;

import mikenakis.immutability.internal.mykit.UncheckedException;
import mikenakis.immutability.annotations.InvariableArray;

import java.lang.reflect.Field;

/**
 * Thrown if a variable field is annotated with the @{@link InvariableArray} annotation.
 * The @{@link InvariableArray} annotation is only valid for invariable fields.
 *
 * @author michael.gr
 */
public class VariableFieldMayNotBeAnnotatedInvariableArrayException extends UncheckedException
{
	public final Field field;

	public VariableFieldMayNotBeAnnotatedInvariableArrayException( Field field )
	{
		this.field = field;
	}
}
