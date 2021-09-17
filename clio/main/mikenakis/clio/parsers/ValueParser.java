package mikenakis.clio.parsers;

public abstract class ValueParser<T>
{
	public abstract boolean isValid( String s );
	public abstract T valueFromString( String s );
	public abstract String stringFromValue( T value );
}
