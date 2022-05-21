package io.github.mikenakis.testana.testplan.intent;

/**
 * Signifies that a test class should run because it never ran before.
 *
 * @author michael.gr
 */
public final class RunBecauseNeverRunBeforeIntent extends RunIntent
{
	public static final RunBecauseNeverRunBeforeIntent INSTANCE = new RunBecauseNeverRunBeforeIntent();

	private RunBecauseNeverRunBeforeIntent()
	{
	}

	@Override public String getMessage()
	{
		return "must run because it never ran before.";
	}

	@Override public boolean equals( Intent other )
	{
		if( other instanceof RunBecauseNeverRunBeforeIntent )
		{
			assert other == this;
			return true;
		}
		return false;
	}
}
