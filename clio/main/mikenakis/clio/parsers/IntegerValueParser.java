package mikenakis.clio.parsers;

import mikenakis.kit.Kit;

public final class IntegerValueParser extends ValueParser<Integer>
{
	public static ValueParser<Integer> instance = new IntegerValueParser();

	private IntegerValueParser()
	{
	}

	@Override public boolean isValid( String s )
	{
		try
		{
			Kit.get( Integer.valueOf( s ) );
			return true;
		}
		catch( NumberFormatException ignore )
		{
			return false;
		}
	}

	@Override public Integer valueFromString( String s )
	{
		return Integer.valueOf( s );
	}

	@Override public String stringFromValue( Integer value )
	{
		return value.toString();
	}
}
