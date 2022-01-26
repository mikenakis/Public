package mikenakis.test.intertwine.comparisons.rig;

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

	@Override public boolean equals( Object otherAsObject )
	{
		assert this != otherAsObject;
		if( otherAsObject == null || getClass() != otherAsObject.getClass() )
			return false;
		Beta other = (Beta)otherAsObject;
		return Objects.equals( s, other.s );
	}

	@Override public int hashCode()
	{
		return Objects.hash( s );
	}
}
