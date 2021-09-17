package mikenakis.bytecode.exceptions;

/**
 * "Unknown Value" exception.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class UnknownValueException extends ByteCodeException
{
	public final int tag;

	public UnknownValueException( int tag )
	{
		this.tag = tag;
	}

	@Override protected String onGetMessage()
	{
		return "Tag: " + tag;
	}
}
