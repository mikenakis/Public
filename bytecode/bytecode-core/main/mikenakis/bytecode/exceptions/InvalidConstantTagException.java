package mikenakis.bytecode.exceptions;

import mikenakis.bytecode.model.Constant;
import mikenakis.kit.UncheckedException;

/**
 * "Invalid {@link Constant} Tag" exception.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class InvalidConstantTagException extends UncheckedException
{
	public final int constantTag;

	public InvalidConstantTagException( int constantTag )
	{
		this.constantTag = constantTag;
	}
}
