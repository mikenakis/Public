package mikenakis.bytecode.exceptions;

/**
 * "Malformed UTF8" {@link ByteCodeException}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class IncompleteUtf8Exception extends ByteCodeException
{
	public final int position;

	public IncompleteUtf8Exception( int position )
	{
		this.position = position;
	}

	@Override protected String onGetMessage()
	{
		return "position: " + position;
	}
}
