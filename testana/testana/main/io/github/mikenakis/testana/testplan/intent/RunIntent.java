package io.github.mikenakis.testana.testplan.intent;

/**
 * "Test Class will run" {@link Intent}.
 *
 * @author michael.gr
 */
public abstract class RunIntent extends Intent
{
	protected RunIntent()
	{
	}

	@Override public final boolean isToRun()
	{
		return true;
	}
}
