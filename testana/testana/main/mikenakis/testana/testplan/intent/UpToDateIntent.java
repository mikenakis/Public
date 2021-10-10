package mikenakis.testana.testplan.intent;

/**
 * Signifies that a test class does not need to run because it is up-to-date.
 * (Neither the test class, nor any of its dependencies, has changed since the last test run.)
 *
 * @author michael.gr
 */
public final class UpToDateIntent extends Intent
{
	public static final UpToDateIntent INSTANCE = new UpToDateIntent();

	private UpToDateIntent()
	{
	}

	@Override public boolean isToRun()
	{
		return false;
	}

	@Override public String getMessage()
	{
		return "does not need to run because none of its dependencies have changed.";
	}

	@Override public boolean equals( Intent other )
	{
		if( other instanceof UpToDateIntent )
		{
			assert other == this;
			return true;
		}
		return false;
	}
}
