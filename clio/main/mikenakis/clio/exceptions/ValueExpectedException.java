package mikenakis.clio.exceptions;

/**
 * "Value expected" {@link ClioException}.
 *
 * @author michael.gr
 */
public class ValueExpectedException extends ClioException
{
	public final String switchName;

	public ValueExpectedException( String switchName )
	{
		this.switchName = switchName;
	}

	@Override public String getMessage()
	{
		return "Value expected after " + switchName;
	}
}
