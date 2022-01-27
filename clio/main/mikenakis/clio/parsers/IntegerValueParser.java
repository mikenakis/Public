package mikenakis.clio.parsers;

import mikenakis.kit.GenericException;
import mikenakis.kit.Kit;

import java.util.Optional;

public final class IntegerValueParser extends ValueParser<Integer>
{
	public static ValueParser<Integer> instance = new IntegerValueParser();

	private IntegerValueParser()
	{
	}

	@Override public Optional<RuntimeException> validate( String s )
	{
		try
		{
			Kit.get( Integer.valueOf( s ) );
			return Optional.empty();
		}
		catch( NumberFormatException ignore )
		{
			return Optional.of( new GenericException( "Expected an integer, found '" + s + "'" ) );
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
