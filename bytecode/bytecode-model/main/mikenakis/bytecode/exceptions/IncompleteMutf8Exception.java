package mikenakis.bytecode.exceptions;

import mikenakis.bytecode.kit.UncheckedException;

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

	@Override protected String onGetMessage()
	{
		return "position: " + position;
	}
}
