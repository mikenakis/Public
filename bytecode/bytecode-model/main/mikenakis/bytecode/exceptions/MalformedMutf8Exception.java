package mikenakis.bytecode.exceptions;

import mikenakis.bytecode.kit.UncheckedException;

/**
 * "Malformed MUTF8" exception.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class MalformedMutf8Exception extends UncheckedException
{
	public final int position;

	public MalformedMutf8Exception( int position )
	{
		this.position = position;
	}

	@Override protected String onGetMessage()
	{
		return "position: " + position;
	}
}
