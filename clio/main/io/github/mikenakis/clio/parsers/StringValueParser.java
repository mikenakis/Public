package io.github.mikenakis.clio.parsers;

import java.util.Optional;

public final class StringValueParser extends ValueParser<String>
{
	public static ValueParser<String> instance = new StringValueParser();

	private StringValueParser()
	{
	}

	@Override public Optional<RuntimeException> validate( String s )
	{
		return Optional.empty();
	}

	@Override public String valueFromString( String s )
	{
		return s;
	}

	@Override public String stringFromValue( String value )
	{
		return value;
	}
}
