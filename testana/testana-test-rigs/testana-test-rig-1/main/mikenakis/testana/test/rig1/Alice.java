package mikenakis.testana.test.rig1;

/**
 * Represents a dependency of a class which is to be tested.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class Alice
{
	private final String suffix;

	public Alice( String suffix )
	{
		this.suffix = suffix;
	}

	public String doYourThing( String s )
	{
		return s + suffix;
	}
}
