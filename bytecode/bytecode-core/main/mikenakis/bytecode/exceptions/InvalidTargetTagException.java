package mikenakis.bytecode.exceptions;

import mikenakis.kit.UncheckedException;

import java.lang.annotation.Target;

/**
 * "Invalid {@link Target} Tag" exception.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class InvalidTargetTagException extends UncheckedException
{
	public final int targetTag;

	public InvalidTargetTagException( int targetTag )
	{
		this.targetTag = targetTag;
	}
}
