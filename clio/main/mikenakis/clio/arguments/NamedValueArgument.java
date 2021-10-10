package mikenakis.clio.arguments;

import mikenakis.clio.exceptions.ValueExpectedException;
import mikenakis.clio.parsers.ValueParser;

import java.util.List;
import java.util.Optional;

/**
 * An {@link BaseArgument} which consists of an argument-name followed by a value.
 *
 * TODO: make it possible to specify "-name=value" instead of only "-name value"
 *
 * @author michael.gr
 */
public final class NamedValueArgument<T> extends ValueArgument<T>
{
	private final List<String> switchNames;

	public NamedValueArgument( List<String> switchNames, String description, ValueParser<T> valueParser )
	{
		super( switchNames.get( 0 ), description, valueParser );
		this.switchNames = switchNames;
	}

	@Override protected Optional<String> getValueToken( List<String> tokens )
	{
		String switchNameToken = tokens.get( 0 );
		if( !switchNames.contains( switchNameToken ) )
			return Optional.empty();
		tokens.remove( 0 );
		if( tokens.isEmpty() )
			throw new ValueExpectedException( switchNameToken );
		return Optional.of( tokens.remove( 0 ) );
	}

	@Override public String getShortUsage()
	{
		return switchNames.get( 0 ) + " <" + name() + ">";
	}

	@Override public boolean isOptional()
	{
		return false;
	}

	@Override public boolean isPositional()
	{
		return false;
	}
}
