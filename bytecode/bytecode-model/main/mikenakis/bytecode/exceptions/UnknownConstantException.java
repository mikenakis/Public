package mikenakis.bytecode.exceptions;

/**
 * "Unknown Constant" exception.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class UnknownConstantException extends ByteCodeException
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
