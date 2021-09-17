package mikenakis.clio.parsers;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class EnumChoiceValueParser<T extends Enum<T>> extends ChoiceValueParser<T>
{
	private final Class<T> enumClass;

	public EnumChoiceValueParser( Class<T> enumClass )
	{
		super( getAcceptedStrings( enumClass ) );
		this.enumClass = enumClass;
	}

	private static <T> Collection<String> getAcceptedStrings( Class<T> enumClass )
	{
		return Arrays.stream( enumClass.getEnumConstants() ).map( c -> c.toString() ).collect( Collectors.toUnmodifiableSet() );
	}

	@Override public String stringFromValue( T value )
	{
		return value.name();
	}

	@Override public T valueFromString( String s )
	{
		return Enum.valueOf( enumClass, s );
	}
}
