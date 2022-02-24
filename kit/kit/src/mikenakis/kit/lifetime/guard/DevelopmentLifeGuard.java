package mikenakis.kit.lifetime.guard;

import mikenakis.kit.debug.Debug;
import mikenakis.kit.lifetime.Closeable;

import java.util.Optional;

/**
 * Development {@link LifeGuard}.
 *
 * @author michael.gr
 */
public abstract class DevelopmentLifeGuard implements LifeGuard.Defaults
{
	public interface LifetimeErrorHandler
	{
		void invoke( Class<? extends Closeable> closeableClass, Optional<StackWalker.StackFrame[]> stackTrace );
	}

	private static LifetimeErrorHandler lifetimeErrorHandler = DevelopmentLifeGuard::defaultLifetimeErrorHandler;

	DevelopmentLifeGuard()
	{
	}

	static LifetimeErrorHandler getLifetimeErrorHandler()
	{
		return lifetimeErrorHandler;
	}

	/**
	 * Sets a custom {@link LifetimeErrorHandler}. (For use by testing code.)
	 */
	public static void setLifetimeErrorHandler( LifetimeErrorHandler lifetimeErrorHandler )
	{
		assert lifetimeErrorHandler != null;
		DevelopmentLifeGuard.lifetimeErrorHandler = lifetimeErrorHandler;
	}

	private static void defaultLifetimeErrorHandler( Class<?> closeableClass, Optional<StackWalker.StackFrame[]> allocationStackTrace )
	{
		String stackTraceText = allocationStackTrace.map( DevelopmentLifeGuard::stackFramesToString ).orElse( "Collection of allocation stack trace is not enabled for this class." );
		Debug.breakPoint( "Closeable class not closed: " + closeableClass + "\n" + stackTraceText );
	}

	private static String stackFramesToString( StackWalker.StackFrame[] stackFrames )
	{
		//NOTE: we should not do anything fancy here, because we may be invoked from within finalize()
		var builder = new StringBuilder();
		for( StackWalker.StackFrame stackFrame : stackFrames )
		{
			builder.append( "\tat " );
			builder.append( stackFrame );
			builder.append( '\n' );
		}
		return builder.toString();
	}
}
