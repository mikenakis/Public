package mikenakis.bytecode.exceptions;

import mikenakis.bytecode.kit.UncheckedException;

/**
 * "Unknown Constant" exception.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class UnknownConstantException extends UncheckedException
{
	public final int tag;

	public UnknownConstantException( int tag )
	{
		this.tag = tag;
	}

	@Override protected String onGetMessage()
	{
		return "Tag: " + tag;
	}
}
