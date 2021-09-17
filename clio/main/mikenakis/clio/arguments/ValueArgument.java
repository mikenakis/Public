package mikenakis.clio.arguments;

import mikenakis.clio.exceptions.UnparsableValueException;
import mikenakis.clio.parsers.ValueParser;

import java.util.List;
import java.util.Optional;

/**
 * An {@link BaseArgument} that has a value.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class ValueArgument<T> extends BaseArgument<T>
{
	private final ValueParser<T> valueParser;
	private Optional<String> valueToken = Optional.empty();

	protected ValueArgument( String name, String description, ValueParser<T> valueParser )
	{
		super( name, description );
		this.valueParser = valueParser;
	}

	protected abstract Optional<String> getValueToken( List<String> tokens );

	@Override public final boolean tryParse( List<String> tokens )
	{
		assert valueToken.isEmpty();
		valueToken = getValueToken( tokens );
		if( valueToken.isEmpty() )
			return false;
		if( !valueParser.isValid( valueToken.get() ) )
			throw new UnparsableValueException( this, valueToken.get() );
		return true;
	}

	@Override public final T get()
	{
		return valueToken.map( s -> valueParser.valueFromString( s ) ).orElseThrow();
	}

	@Override public final String getLongUsage()
	{
		return description();
	}
}
