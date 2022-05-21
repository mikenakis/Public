package io.github.mikenakis.clio.parsers;

import java.util.Collection;

public class StringChoiceValueParser extends ChoiceValueParser<String>
{
	public StringChoiceValueParser( Collection<String> acceptedStrings )
	{
		super( acceptedStrings );
	}

	@Override public String stringFromValue( String value )
	{
		assert acceptedStrings.contains( value );
		return value;
	}

	@Override public String valueFromString( String s )
	{
		assert acceptedStrings.contains( s );
		return s;
	}
}
