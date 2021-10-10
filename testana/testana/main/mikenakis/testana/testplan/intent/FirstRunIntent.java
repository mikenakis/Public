package mikenakis.testana.testplan.intent;

/**
 * Signifies that a test class should run because it never ran before.
 *
 * @author michael.gr
 */
public final class FirstRunIntent extends RunIntent
{
	public static final FirstRunIntent INSTANCE = new FirstRunIntent();

	private FirstRunIntent()
	{
	}

	@Override public String getMessage()
	{
		return "must run because it never ran before.";
	}

	@Override public boolean equals( Intent other )
	{
		if( other instanceof FirstRunIntent )
		{
			assert other == this;
			return true;
		}
		return false;
	}
}
