package mikenakis.kit.lifetime.guard;

import mikenakis.kit.Kit;
import mikenakis.kit.lifetime.Mortal;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Optional;

/**
 * A {@link DevelopmentLifeGuard} that works via finalization, which has been deprecated as of Java 9.
 *
 * @author michael.gr
 */
public final class FinalizingDevelopmentLifeGuard extends DevelopmentLifeGuard
{
	public static FinalizingDevelopmentLifeGuard of( Mortal mortal, Optional<StackWalker.StackFrame[]> stackTrace )
	{
		return new FinalizingDevelopmentLifeGuard( mortal, stackTrace );
	}

	private final Class<? extends Mortal> mortalClass;
	private final Optional<StackWalker.StackFrame[]> stackTrace;
	private final String mortalIdentity;
	private boolean closed;

	private FinalizingDevelopmentLifeGuard( Mortal mortal, Optional<StackWalker.StackFrame[]> stackTrace )
	{
		super( mortal.coherence() );
		mortalClass = mortal.getClass();
		this.stackTrace = stackTrace;
		mortalIdentity = Kit.identityString( mortal );
	}

	@SuppressWarnings( "deprecation" ) @OverridingMethodsMustInvokeSuper @Deprecated @Override protected void finalize() throws Throwable
	{
		System.out.println( Kit.identityString( this ) + ": finalization of " + mortalIdentity + ": closed = " + closed );
		if( !closed )
			getLifetimeErrorHandler().invoke( mortalClass, stackTrace );
		super.finalize();
	}

	@Override public void close()
	{
		assert !closed;
		closed = true;
	}

	@Override public boolean mustBeAliveAssertion()
	{
		assert !closed : new MustBeAliveException( mortalClass );
		return true;
	}

	@Override public String toString()
	{
		return (closed ? "not " : "") + "alive";
	}
}
