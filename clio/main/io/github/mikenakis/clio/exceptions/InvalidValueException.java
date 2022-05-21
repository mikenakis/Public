package io.github.mikenakis.clio.exceptions;

import java.util.List;

/**
 * "Invalid value" {@link ClioException}.
 *
 * @author michael.gr
 */
public class InvalidValueException extends ClioException
{
	public final String foundValue;
	public final List<String> expectedValues;

	public InvalidValueException( String foundValue, List<String> expectedValues )
	{
		this.foundValue = foundValue;
		this.expectedValues = expectedValues;
	}

	@Override public String getMessage()
	{
		return "Invalid value '" + foundValue + "'; expected one of " + expectedValues;
	}
}
