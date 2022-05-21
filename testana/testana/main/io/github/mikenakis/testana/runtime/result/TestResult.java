package io.github.mikenakis.testana.runtime.result;

import java.time.Duration;

/**
 * Test Result.
 *
 * @author michael.gr
 */
public abstract class TestResult
{
	public final String name;
	private Duration duration = null;

	TestResult( String name )
	{
		this.name = name;
	}

	public int successCount() { return 0; }
	public int ignoredCount() { return 0; }
	public int failureCount() { return 0; }
	public int upToDateCount() { return 0; }
	public int totalCount() { return 1; }

	public void setDuration( Duration duration ) //TODO: make use of!
	{
		assert duration != null;
		assert this.duration == null;
		this.duration = duration;
	}

	public Duration getDuration()
	{
		return duration;
	}

	public abstract String getOutcomeMessage();

	@Override public final String toString()
	{
		return getOutcomeMessage();
	}
}
