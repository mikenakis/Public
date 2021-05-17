package mikenakis.bytecode.exceptions;

/**
 * "String too long" {@link ByteCodeException}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class StringTooLongException extends ByteCodeException
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
