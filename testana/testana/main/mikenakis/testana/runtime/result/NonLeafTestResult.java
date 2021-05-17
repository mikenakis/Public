package mikenakis.testana.runtime.result;

import java.util.Collection;

/**
 * Non-leaf (class or root) {@link TestResult}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class NonLeafTestResult extends TestResult
{
	private final Collection<? extends TestResult> children;
	private final int successCount;
	private final int ignoredCount;
	private final int failureCount;
	private final int upToDateCount;
	private final int totalCount;

	public NonLeafTestResult( String name, Collection<? extends TestResult> children )
	{
		super( name );
		this.children = children;
		int successCountLocal = 0;
		int ignoredCountLocal = 0;
		int failureCountLocal = 0;
		int upToDateCountLocal = 0;
		int totalCountLocal = 0;
		for( TestResult testResult : children )
		{
			successCountLocal += testResult.successCount();
			ignoredCountLocal += testResult.ignoredCount();
			failureCountLocal += testResult.failureCount();
			upToDateCountLocal += testResult.upToDateCount();
			totalCountLocal += testResult.totalCount();
		}
		successCount = successCountLocal;
		ignoredCount = ignoredCountLocal;
		failureCount = failureCountLocal;
		upToDateCount = upToDateCountLocal;
		totalCount = totalCountLocal;
	}

	public Collection<? extends TestResult> children()
	{
		return children;
	}

	@Override public int successCount()
	{
		return successCount;
	}

	@Override public int ignoredCount()
	{
		return ignoredCount;
	}

	@Override public int failureCount()
	{
		return failureCount;
	}

	@Override public int upToDateCount()
	{
		return upToDateCount;
	}

	@Override public int totalCount()
	{
		return totalCount;
	}

	@Override public String getOutcomeMessage()
	{
		var builder = new StringBuilder();
		if( ignoredCount == totalCount )
			builder.append( "All ignored" );
		else if( upToDateCount == totalCount )
			builder.append( "All up-to-date" );
		else
			builder.append( failureCount == 0 ? "Success" : "Failure" );
		builder.append( " (" );
		builder.append( successCount ).append( " succeeded, " );
		builder.append( failureCount ).append( " failed, " );
		builder.append( ignoredCount ).append( " ignored, " );
		builder.append( upToDateCount ).append( " up-to-date, " );
		builder.append( totalCount ).append( " total" );
		builder.append( ")" );
		return builder.toString();
	}
}
