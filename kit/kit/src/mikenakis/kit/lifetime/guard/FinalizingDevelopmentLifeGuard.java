package mikenakis.kit.lifetime.guard;

import mikenakis.kit.Kit;
import mikenakis.kit.lifetime.Closeable;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Optional;

/**
 * A {@link DevelopmentLifeGuard} that works via finalization, which has been deprecated as of Java 9.
 *
 * @author michael.gr
 */
public final class FinalizingDevelopmentLifeGuard extends DevelopmentLifeGuard
{
	public static FinalizingDevelopmentLifeGuard of( Closeable closeable, Optional<StackWalker.StackFrame[]> stackTrace )
	{
		return new FinalizingDevelopmentLifeGuard( closeable, stackTrace );
	}

	private final Class<? extends Closeable> closeableClass;
	private final Optional<StackWalker.StackFrame[]> stackTrace;
	private final String closeableIdentity;
	private boolean closed;

	private FinalizingDevelopmentLifeGuard( Closeable closeable, Optional<StackWalker.StackFrame[]> stackTrace )
	{
		closeableClass = closeable.getClass();
		this.stackTrace = stackTrace;
		closeableIdentity = Kit.identityString( closeable );
	}

	@SuppressWarnings( "deprecation" ) @OverridingMethodsMustInvokeSuper @Deprecated @Override protected void finalize() throws Throwable
	{
		System.out.println( Kit.identityString( this ) + ": finalization of " + closeableIdentity + ": closed = " + closed );
		if( !closed )
			getLifetimeErrorHandler().invoke( closeableClass, stackTrace );
		super.finalize();
	}

	@Override public void close()
	{
		assert !closed;
		closed = true;
	}

	@Override public boolean mustBeAliveAssertion()
	{
		assert !closed : new MustBeAliveException( closeableClass );
		return true;
	}

	@Override public String toString()
	{
		return (closed ? "not " : "") + "alive";
	}
}
