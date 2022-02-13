package mikenakis.testana.test.rigs.classes_under_test;

/**
 * Represents a dependency of a class which is to be tested.
 *
 * @author michael.gr
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
