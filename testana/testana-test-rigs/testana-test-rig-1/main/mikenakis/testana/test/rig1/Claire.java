package mikenakis.testana.test.rig1;

/**
 * Represents a class which is to be tested.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class Claire
{
	public static class Betty
	{
		private final Alice alice;

		Betty( String suffix )
		{
			alice = new Alice( suffix );
		}

		public String doSomeThing( String s )
		{
			return alice.doYourThing( s );
		}
	}

	private final Betty betty;

	public Claire( String suffix )
	{
		betty = new Betty( suffix );
	}

	public String doThatThing( String s )
	{
		return betty.doSomeThing( s );
	}
}
