package mikenakis.clio.arguments;

import mikenakis.clio.Clio;

import java.util.List;
import java.util.stream.Collectors;

/**
 * An {@link ParameterOption} whose parameter is a value belonging to a specific set of values.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class ChoiceParameterOption<T> extends ParameterOption<T>
{
	protected final List<T> acceptedParameterValues;

	protected ChoiceParameterOption( Clio clio, String optionName, String parameterName, List<T> acceptedParameterValues, T defaultParameterValue, String description )
	{
		super( clio, optionName, parameterName, defaultParameterValue, description );
		assert defaultParameterValue == null || acceptedParameterValues.contains( defaultParameterValue );
		this.acceptedParameterValues = acceptedParameterValues;
	}

	@Override protected String getDescriptionSuffix()
	{
		return ", one of: " + acceptedParameterValues.stream().map( value -> stringFromValue( value ) ).collect( Collectors.toList() );
	}

	protected abstract String stringFromValue( T value );
}
