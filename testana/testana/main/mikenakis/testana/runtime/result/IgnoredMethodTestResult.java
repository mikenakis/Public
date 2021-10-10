package mikenakis.testana.runtime.result;

/**
 * 'Ignored' {@link TestMethodResult}.
 *
 * @author michael.gr
 */
public final class IgnoredMethodTestResult extends TestMethodResult
{
	public IgnoredMethodTestResult( String name )
	{
		super( name );
	}

	@Override public int ignoredCount()
	{
		return 1;
	}

	@Override public String getOutcomeMessage()
	{
		return "Ignored";
	}
}
