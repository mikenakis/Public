package mikenakis.clio.exceptions;

import mikenakis.clio.arguments.Argument;
import mikenakis.clio.arguments.BaseArgument;

/**
 * "Unparsable value" {@link ClioException}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class UnparsableValueException extends ClioException
{
	public final Argument<?> argument;
	public final String valueToken;

	public UnparsableValueException( Argument<?> argument, String valueToken )
	{
		this.argument = argument;
		this.valueToken = valueToken;
	}

	@Override public String getMessage()
	{
		return "Argument " + argument.name() + " cannot receive a value of '" + valueToken + "'.";
	}
}
