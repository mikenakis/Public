package mikenakis.clio.exceptions;

import mikenakis.clio.arguments.Argument;

/**
 * "Required argument missing" {@link ClioException}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class RequiredArgumentMissingException extends ClioException
{
	public final Argument<?> argument;

	public RequiredArgumentMissingException( Argument<?> argument )
	{
		this.argument = argument;
	}

	@Override public String getMessage()
	{
		return "Required argument missing: " + argument.name();
	}
}
