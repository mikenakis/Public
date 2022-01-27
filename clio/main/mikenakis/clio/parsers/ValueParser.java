package mikenakis.clio.parsers;

import java.util.Optional;

public abstract class ValueParser<T>
{
	public abstract Optional<RuntimeException> validate( String s );
	public abstract T valueFromString( String s );
	public abstract String stringFromValue( T value );
}
