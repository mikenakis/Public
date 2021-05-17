package mikenakis.kit;

import java.util.Optional;

/**
 * "Exception Differs from Expected" {@link UncheckedException}.
 *
 * @author Michael Belivanakis (michael.gr)
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
