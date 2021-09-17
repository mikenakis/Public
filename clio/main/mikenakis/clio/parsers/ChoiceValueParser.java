package mikenakis.clio.parsers;

import java.util.Collection;

public abstract class ChoiceValueParser<T> extends ValueParser<T>
{
	protected final Collection<String> acceptedStrings;

	protected ChoiceValueParser( Collection<String> acceptedStrings )
	{
		this.acceptedStrings = acceptedStrings;
	}

	@Override public boolean isValid( String s )
	{
		return acceptedStrings.contains( s );
	}
}
