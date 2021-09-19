package mikenakis.bytecode.exceptions;

/**
 * "Incomplete MUTF8" {@link ByteCodeException}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class IncompleteMutf8Exception extends ByteCodeException
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
