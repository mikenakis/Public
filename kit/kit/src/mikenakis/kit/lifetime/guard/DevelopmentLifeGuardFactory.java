package mikenakis.kit.lifetime.guard;

import mikenakis.kit.Kit;
import mikenakis.kit.debug.Debug;
import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.logging.Log;

import java.util.Optional;

/**
 * Development {@link LifeGuardFactory}.
 *
 * @author michael.gr
 */
public abstract class DevelopmentLifeGuardFactory implements LifeGuardFactory.Defaults
{
	public interface FinalizationHandler
	{
		void invoke( Class<? extends Closeable> closeableClass, boolean x, Optional<StackWalker.StackFrame[]> stackTrace );
	}

	private FinalizationHandler finalizationHandler = DevelopmentLifeGuardFactory::defaultFinalizationHandler;

	DevelopmentLifeGuardFactory()
	{
	}

	FinalizationHandler getFinalizationHandler()
	{
		return finalizationHandler;
	}

	/**
	 * Sets a finalization handler. (For use by testing code.)
	 */
	public void setFinalizationHandler( FinalizationHandler finalizationHandler )
	{
		assert finalizationHandler != null;
		this.finalizationHandler = finalizationHandler;
	}

	private static void defaultFinalizationHandler( Class<?> closeableClass, boolean closed, Optional<StackWalker.StackFrame[]> allocationStackTrace )
	{
		if( closed )
			return;
		String stackTraceText = allocationStackTrace.map( DevelopmentLifeGuardFactory::stackFramesToString ).orElse( "Collection of allocation stack trace is not enabled for this class." );
		Log.error( "Closeable class not closed: " + closeableClass + "\n" + stackTraceText ); //TODO: use logging.
		Debug.breakPoint();
	}

	protected abstract LifeGuard onNewDevelopmentLifeGuard( Closeable closeable, boolean initiallyAlive, Optional<StackWalker.StackFrame[]> stackTrace );

	@Override public final LifeGuard newLifeGuard( int framesToSkip, Closeable closeable, boolean collectStackTrace, boolean initiallyAlive )
	{
		Optional<StackWalker.StackFrame[]> stackTrace = collectStackTrace ? Optional.of( Kit.getStackTrace( framesToSkip + 2 ) ) : Optional.empty();
		return onNewDevelopmentLifeGuard( closeable, initiallyAlive, stackTrace );
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
