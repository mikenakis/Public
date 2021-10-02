package mikenakis.test.lambdatwine.rig;

import java.util.Objects;

public class Alpha //NOTE: the JSON mapper requires that this be public!
{
	public final String s;

	public Alpha( String s )
	{
		this.s = s;
	}

	public Alpha( Alpha other )
	{
		s = other.s;
	}

	@Override public boolean equals( Object otherAsObject )
	{
		assert this != otherAsObject;
		if( otherAsObject == null || getClass() != otherAsObject.getClass() )
			return false;
		Alpha other = (Alpha)otherAsObject;
		return Objects.equals( s, other.s );
	}

	@Override public int hashCode()
	{
		return Objects.hash( s );
	}
}
