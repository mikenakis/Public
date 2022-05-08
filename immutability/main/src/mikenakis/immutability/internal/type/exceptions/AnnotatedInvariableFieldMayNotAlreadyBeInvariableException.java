package mikenakis.immutability.internal.type.exceptions;

import mikenakis.immutability.internal.mykit.UncheckedException;
import mikenakis.immutability.annotations.Invariable;

import java.lang.reflect.Field;

/**
 * Thrown when a {@code final} field is annotated with @{@link Invariable}.
 * A {@code final} final need not, and should not, be annotated with @{@link Invariable}.
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
