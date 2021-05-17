package mikenakis.clio.exceptions;

import java.util.List;

/**
 * "Unparsable Parameter" {@link ClioException}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class UnparsableParameterException extends ClioException
{
	public final String parameterName;
	public final String token;
	public final List<String> validTokens;

	public UnparsableParameterException( String parameterName, String token, List<String> validTokens )
	{
		this.parameterName = parameterName;
		this.token = token;
		this.validTokens = validTokens;
	}

	@Override public String getMessage()
	{
		return "Could not parse '" + token + "' as " + parameterName + ". Valid values: " + validTokens;
	}
}
