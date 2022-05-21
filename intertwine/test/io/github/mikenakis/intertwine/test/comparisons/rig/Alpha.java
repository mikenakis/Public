package io.github.mikenakis.intertwine.test.comparisons.rig;

import java.util.Objects;

public class Alpha //NOTE: the JSON mapper requires that this be public!
{
	public final String s;
	public final Beta b;

	public Alpha( String s, Beta b )
	{
		this.s = s;
		this.b = b;
	}

	public Alpha( Alpha other )
	{
		s = other.s;
		b = new Beta( other.b );
	}

	@Override public boolean equals( Object otherAsObject )
	{
		assert this != otherAsObject;
		if( otherAsObject == null || getClass() != otherAsObject.getClass() )
			return false;
		Alpha other = (Alpha)otherAsObject;
		return Objects.equals( s, other.s ) && Objects.equals( b, other.b );
	}

	@Override public int hashCode()
	{
		return Objects.hash( s, b );
	}
}
