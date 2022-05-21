package io.github.mikenakis.testana.runtime.result;

/**
 * Success {@link TestMethodResult}.
 *
 * @author michael.gr
 */
public final class SucceededMethodTestResult extends TestMethodResult
{
	public SucceededMethodTestResult( String name )
	{
		super( name );
	}

	@Override public int successCount()
	{
		return 1;
	}

	@Override public String getOutcomeMessage()
	{
		return "Success";
	}
}
