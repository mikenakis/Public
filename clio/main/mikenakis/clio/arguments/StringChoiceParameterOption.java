package mikenakis.clio.arguments;

import mikenakis.clio.Clio;

import java.util.List;

/**
 * An {@link ParameterOption} whose parameter is a string belonging to a specific set of strings.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class StringChoiceParameterOption extends ChoiceParameterOption<String>
{
	public StringChoiceParameterOption( Clio clio, String optionName, String parameterName, List<String> parameterValues, String defaultParameterValue,
		String description )
	{
		super( clio, optionName, parameterName, parameterValues, defaultParameterValue, description );
	}

	@Override protected String stringFromValue( String value )
	{
		return value;
	}

	@Override protected String valueFromString( String value )
	{
		return value;
	}
}
