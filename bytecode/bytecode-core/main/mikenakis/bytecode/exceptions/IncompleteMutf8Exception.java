package mikenakis.bytecode.exceptions;

import mikenakis.kit.UncheckedException;

/**
 * "Incomplete MUTF8" exception.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class IncompleteMutf8Exception extends UncheckedException
{
	public final int position;

	public IncompleteMutf8Exception( int position )
	{
		this.position = position;
	}
}
