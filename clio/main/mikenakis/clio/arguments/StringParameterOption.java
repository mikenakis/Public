package mikenakis.clio.arguments;

import mikenakis.clio.Clio;

/**
 * An {@link ParameterOption} whose parameter can be any string.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class StringParameterOption extends ParameterOption<String>
{
	public StringParameterOption( Clio clio, String optionName, String parameterName, String defaultParameterValue, String description )
	{
		super( clio, optionName, parameterName, defaultParameterValue, description );
	}

	@Override protected String valueFromString( String value )
	{
		return value;
	}
}
