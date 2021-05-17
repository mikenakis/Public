package mikenakis.clio.exceptions;

import java.util.List;

/**
 * "Invalid Parameter" {@link ClioException}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class IllegalParameterException extends ClioException
{
	public final String token;
	public final List<String> values;

	public IllegalParameterException( String token, List<String> values )
	{
		this.token = token;
		this.values = values;
	}

	@Override public String getMessage()
	{
		return "Invalid parameter '" + token + "'; expected one of " + values;
	}
}
