package mikenakis.clio.parsers;

public final class StringValueParser extends ValueParser<String>
{
	public static ValueParser<String> instance = new StringValueParser();

	private StringValueParser()
	{
	}

	@Override public boolean isValid( String s )
	{
		return true;
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
