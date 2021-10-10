package mikenakis.testana.runtime.result;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Failure {@link TestMethodResult}.
 *
 * @author michael.gr
 */
public final class FailedMethodTestResult extends TestMethodResult
{
	private final Throwable throwable;

	public FailedMethodTestResult( String name, Throwable throwable )
	{
		super( name );
		this.throwable = throwable;
	}

	@Override public int failureCount()
	{
		return 1;
	}

	@Override public String getOutcomeMessage()
	{
		StringWriter stringWriter = new StringWriter();
		stringWriter.append( "Failure:\n" );
		PrintWriter printWriter = new PrintWriter( stringWriter );
		throwable.printStackTrace( printWriter );
		return stringWriter.toString();
	}
}
