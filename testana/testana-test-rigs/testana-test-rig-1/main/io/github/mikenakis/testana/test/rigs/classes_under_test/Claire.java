package io.github.mikenakis.testana.test.rigs.classes_under_test;

/**
 * Represents a class which is to be tested.
 *
 * @author michael.gr
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
