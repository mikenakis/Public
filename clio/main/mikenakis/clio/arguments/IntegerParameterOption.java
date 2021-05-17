package mikenakis.clio.arguments;

import mikenakis.clio.Clio;

/**
 * An {@link ParameterOption} whose parameter is an integer.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class IntegerParameterOption extends ParameterOption<Integer>
{
	public IntegerParameterOption( Clio clio, String optionName, String parameterName, Integer defaultParameterValue, String description )
	{
		super( clio, optionName, parameterName, defaultParameterValue, description );
	}

	@Override protected Integer valueFromString( String value )
	{
		try
		{
			return Integer.valueOf( value );
		}
		catch( NumberFormatException ignore )
		{
			return null;
		}
	}
}
