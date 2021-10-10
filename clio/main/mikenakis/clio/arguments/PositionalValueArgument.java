package mikenakis.clio.arguments;

import mikenakis.clio.parsers.ValueParser;

import java.util.List;
import java.util.Optional;

/**
 * A positional {@link BaseArgument}.
 *
 * @author michael.gr
 */
public final class PositionalValueArgument<T> extends ValueArgument<T>
{
	public PositionalValueArgument( String name, String description, ValueParser<T> valueParser )
	{
		super( name, description, valueParser );
	}

	@Override protected Optional<String> getValueToken( List<String> tokens )
	{
		return Optional.of( tokens.remove( 0 ) );
	}

	@Override public String getShortUsage()
	{
		return "<" + name() + ">";
	}

	@Override public boolean isPositional()
	{
		return true;
	}

	@Override public boolean isOptional()
	{
		return false;
	}
}
