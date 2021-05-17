package mikenakis.clio.arguments;

import mikenakis.clio.Clio;
import mikenakis.clio.exceptions.UnparsableParameterException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * An {@link ChoiceParameterOption} whose parameter is an enum value.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class EnumChoiceParameterOption<T extends Enum<T>> extends ChoiceParameterOption<T>
{
	private final Class<T> enumClass;

	public EnumChoiceParameterOption( Clio clio, String optionName, String parameterName, Class<T> enumClass, T defaultParameterValue, String description )
	{
		super( clio, optionName, parameterName, Arrays.asList( enumClass.getEnumConstants() ), defaultParameterValue, description );
		this.enumClass = enumClass;
	}

	@Override protected String stringFromValue( T value )
	{
		return value.name();
	}

	@Override protected T valueFromString( String valueAsString ) throws UnparsableParameterException
	{
		try
		{
			return Enum.valueOf( enumClass, valueAsString );
		}
		catch( IllegalArgumentException ignore2 )
		{
			var validTokens = acceptedParameterValues.stream().map( t -> t.name() ).collect( Collectors.toList() );
			throw new UnparsableParameterException( parameterName, valueAsString, validTokens );
		}
	}
}
