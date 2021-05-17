package mikenakis.testana.runtime.result;

/**
 * A {@link TestMethodResult} indicating that a method is up-to-date, in other words it should be skipped.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class UpToDateMethodTestResult extends TestMethodResult
{
	public UpToDateMethodTestResult( String name )
	{
		super( name );
	}

	@Override public int upToDateCount()
	{
		return 1;
	}

	@Override public String getOutcomeMessage()
	{
		return "Up-To-Date";
	}
}
