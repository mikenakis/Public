package mikenakis.clio.exceptions;

/**
 * "Unrecognized token" {@link ClioException}.
 *
 * @author michael.gr
 */
public class UnrecognizedTokenException extends ClioException
{
	public final String token;

	public UnrecognizedTokenException( String token )
	{
		this.token = token;
	}

	@Override public String getMessage()
	{
		return "Unrecognized token: " + token;
	}
}
