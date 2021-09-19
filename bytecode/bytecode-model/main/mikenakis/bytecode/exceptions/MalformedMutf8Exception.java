package mikenakis.bytecode.exceptions;

/**
 * "Malformed MUTF8" {@link ByteCodeException}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class MalformedMutf8Exception extends ByteCodeException
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
