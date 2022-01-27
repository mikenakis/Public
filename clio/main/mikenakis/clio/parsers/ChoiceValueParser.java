package mikenakis.clio.parsers;

import mikenakis.kit.GenericException;

import java.util.Collection;
import java.util.Optional;

public abstract class ChoiceValueParser<T> extends ValueParser<T>
{
	protected final Collection<String> acceptedStrings;

	protected ChoiceValueParser( Collection<String> acceptedStrings )
	{
		this.acceptedStrings = acceptedStrings;
	}

	@Override public Optional<RuntimeException> validate( String s )
	{
		if( !acceptedStrings.contains( s ) )
			return Optional.of( new GenericException( "Expected one of (" + String.join( ", ", acceptedStrings ) + "), found '" + s + "'" ) );
		return Optional.empty();
	}
}
