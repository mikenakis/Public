package mikenakis.kit.lifetime.guard;

import mikenakis.kit.Kit;
import mikenakis.kit.lifetime.Closeable;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Optional;

/**
 * A {@link DevelopmentLifeGuardFactory} that works via finalization, which has been deprecated as of Java 9.
 *
 * @author michael.gr
 */
public class FinalizingDevelopmentLifeGuardFactory extends DevelopmentLifeGuardFactory
{
	public static final DevelopmentLifeGuardFactory instance = new FinalizingDevelopmentLifeGuardFactory();

	public FinalizingDevelopmentLifeGuardFactory() //constructor is public to allow construction by tests.
	{
	}

	private final class Guard implements LifeGuard, Closeable.Defaults
	{
		private final Class<? extends Closeable> closeableClass;
		private final String closeableIdentity;
		private final Optional<StackWalker.StackFrame[]> stackTrace;
		private boolean closed;

		Guard( Class<? extends Closeable> closeableClass, String closeableIdentity, Optional<StackWalker.StackFrame[]> stackTrace )
		{
			assert closeableClass != null;
			this.closeableClass = closeableClass;
			this.closeableIdentity = closeableIdentity;
			this.stackTrace = stackTrace;
		}

		@SuppressWarnings( "deprecation" )
		@OverridingMethodsMustInvokeSuper
		@Deprecated @Override
		protected void finalize() throws Throwable
		{
			System.out.println( Kit.identityString( this ) + ": finalization of " + closeableIdentity + ": closed = " + closed );
			getFinalizationHandler().invoke( closeableClass, closed, stackTrace );
			super.finalize();
		}

		@Override public void close()
		{
			assert !closed;
			closed = true;
		}

		@Override public boolean lifeStateAssertion( boolean value )
		{
			assert value != closed;
			return true;
		}

		@Override public String toString()
		{
			return (closed ? "not " : "") + "alive";
		}
	}

	@Override public LifeGuard onNewDevelopmentLifeGuard( Closeable closeable, Optional<StackWalker.StackFrame[]> stackTrace )
	{
		String closeableIdentity = Kit.identityString( closeable );
		Guard guard = new Guard( closeable.getClass(), closeableIdentity, stackTrace );
		System.out.println( "New guard: " + Kit.identityString( guard ) + " for closeable " + closeableIdentity );
		return guard;
	}
}
