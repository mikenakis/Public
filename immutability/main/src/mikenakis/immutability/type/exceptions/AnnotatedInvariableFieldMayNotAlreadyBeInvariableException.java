package mikenakis.immutability.type.exceptions;

import mikenakis.immutability.mykit.UncheckedException;
import mikenakis.immutability.type.annotations.InvariableField;

import java.lang.reflect.Field;

/**
 * Thrown when an invariable field is annotated with @{@link InvariableField}.
 * An invariable final need not, and should not, be annotated with @{@link InvariableField}.
 *
 * @author michael.gr
 */
public class AnnotatedInvariableFieldMayNotAlreadyBeInvariableException extends UncheckedException
{
	public final Field field;

	public AnnotatedInvariableFieldMayNotAlreadyBeInvariableException( Field field )
	{
		this.field = field;
	}
}
