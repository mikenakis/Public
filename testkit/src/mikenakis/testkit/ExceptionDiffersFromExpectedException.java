package mikenakis.testkit;

import mikenakis.kit.UncheckedException;

import java.util.Optional;

/**
 * "Exception Differs from Expected" {@link UncheckedException}.
 *
 * @author michael.gr
 */
public class ExceptionDiffersFromExpectedException extends UncheckedException
{
	public final Class<? extends Throwable> expectedExceptionClass;
	public final Throwable actualException;

	public ExceptionDiffersFromExpectedException( Class<? extends Throwable> expectedExceptionClass, Throwable actualException )
	{
		super( Optional.of( actualException ) );
		this.expectedExceptionClass = expectedExceptionClass;
		this.actualException = actualException;
	}
}
