package io.github.mikenakis.kit;

import java.util.Objects;

/**
 * A pair of values.
 *
 * @author michael.gr
 */
public class Dyad<A, B>
{
	public static <A,B> Dyad<A,B> of( A a, B b )
	{
		return new Dyad<>( a, b );
	}

	public final A a;
	public final B b;

	private Dyad( A a, B b )
	{
		this.a = a;
		this.b = b;
	}

	public boolean equals( Dyad<A,B> other )
	{
		if( other == null )
			return false;
		if( this == other )
			return true;
		return equals( other.a, other.b );
	}

	public boolean equals( A otherA, B otherB )
	{
		if( !Objects.equals( a, otherA ) )
			return false;
		if( !Objects.equals( b, otherB ) )
			return false;
		return true;
	}

	@Override public boolean equals( Object obj )
	{
		if( obj == null )
			return false;
		if( obj instanceof Dyad )
		{
			@SuppressWarnings( "unchecked" )
			Dyad<A,B> other = (Dyad<A,B>)obj;
			return equals( other.a, other.b );
		}
		assert false;
		return false;
	}

	@Override public int hashCode()
	{
		return Objects.hash( a, b );
	}

	@Override public String toString()
	{
		return "(" + a + ", " + b + ")";
	}
}
