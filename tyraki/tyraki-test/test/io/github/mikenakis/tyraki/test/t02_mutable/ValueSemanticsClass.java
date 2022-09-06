package io.github.mikenakis.tyraki.test.t02_mutable;

/**
 * An element with value semantics.
 *
 * @author michael.gr
 */
public final class ValueSemanticsClass
{
	private final int value;

	public ValueSemanticsClass( int value )
	{
		this.value = value;
	}

	public boolean equals( ValueSemanticsClass other )
	{
		if( other == null )
			return false;
		if( this == other )
			return true;
		if( value != other.value )
			return false;
		return true;
	}

	@Deprecated @Override public boolean equals( Object other ) { return other instanceof ValueSemanticsClass kin && equals( kin ); }
	@Override public int hashCode() { return Integer.hashCode( value ); }
	@Override public String toString() { return "{" + getClass().getSimpleName() + " " + value + "}"; }
}
