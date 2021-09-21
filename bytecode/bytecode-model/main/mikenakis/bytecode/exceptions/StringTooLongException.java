package mikenakis.bytecode.exceptions;

import mikenakis.bytecode.kit.UncheckedException;

/**
 * "String too long" exception.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class StringTooLongException extends UncheckedException
{
	public final int length;

	public StringTooLongException( int length )
	{
		this.length = length;
	}

	@Override protected String onGetMessage()
	{
		return "length: " + length;
	}
}
