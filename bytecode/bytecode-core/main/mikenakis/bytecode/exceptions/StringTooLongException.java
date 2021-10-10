package mikenakis.bytecode.exceptions;

import mikenakis.kit.UncheckedException;

/**
 * "String too long" exception.
 *
 * @author michael.gr
 */
public final class StringTooLongException extends UncheckedException
{
	public final int length;

	public StringTooLongException( int length )
	{
		this.length = length;
	}
}
