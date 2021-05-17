package mikenakis.clio.exceptions;

import mikenakis.clio.arguments.Option;

/**
 * "Required argument missing" {@link ClioException}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class RequiredArgumentMissingException extends ClioException
{
	public final Option<?> option;

	public RequiredArgumentMissingException( Option<?> option )
	{
		this.option = option;
	}

	@Override public String getMessage()
	{
		return "Required argument missing: " + option.name;
	}
}
