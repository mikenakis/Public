package mikenakis.bytecode.kit;

/**
 * My own exception type.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class UncheckedException extends RuntimeException
{
	public UncheckedException()
	{
		this( null );
	}

	public UncheckedException( Throwable cause )
	{
		super( cause );
	}

	@Override public final String getMessage()
	{
		String details = onGetMessage();
		String result = getClass().getName();
		if( !details.isEmpty() )
			result += ": " + details;
		return result;
	}

	protected String onGetMessage()
	{
		return "";
	}

	@Override public final String toString()
	{
		return getMessage();
	}
}
