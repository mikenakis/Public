package mikenakis.clio.exceptions;

/**
 * "Expected parameter" {@link ClioException}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class ExpectedParameterException extends ClioException
{
	public final String optionName;
	public final String parameterName;

	public ExpectedParameterException( String optionName, String parameterName )
	{
		this.optionName = optionName;
		this.parameterName = parameterName;
	}

	@Override public String getMessage()
	{
		return "Expected " + parameterName + " after " + optionName;
	}
}
