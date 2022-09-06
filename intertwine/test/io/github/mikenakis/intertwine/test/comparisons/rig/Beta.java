package io.github.mikenakis.intertwine.test.comparisons.rig;

import java.util.Objects;

public class Beta //NOTE: the JSON mapper requires that this be public!
{
	public final String s;

	public Beta( String s )
	{
		this.s = s;
	}

	public Beta( Beta other )
	{
		this( other.s );
	}

	@Deprecated @Override public boolean equals( Object otherAsObject )
	{
		assert this != otherAsObject;
		return otherAsObject instanceof Beta kin && equals( kin );
	}

	public boolean equals( Beta other )
	{
		return Objects.equals( s, other.s );
	}

	@Override public int hashCode()
	{
		return Objects.hash( s );
	}
}
