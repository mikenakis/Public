package io.github.mikenakis.live.guard;

import io.github.mikenakis.coherence.AbstractCoherent;
import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.debug.Debug;
import io.github.mikenakis.kit.SourceLocation;
import io.github.mikenakis.kit.logging.Log;
import io.github.mikenakis.live.Mortal;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Development {@link LifeGuard}.
 *
 * @author michael.gr
 */
public abstract class DevelopmentLifeGuard extends AbstractCoherent implements LifeGuard.Defaults
{
	public interface LifetimeErrorHandler
	{
		void invoke( Class<? extends Mortal> mortalClass, Collection<SourceLocation> stackTrace );
	}

	private static LifetimeErrorHandler lifetimeErrorHandler = DevelopmentLifeGuard::defaultLifetimeErrorHandler;

	DevelopmentLifeGuard( Coherence coherence )
	{
		super( coherence );
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

	private static void defaultLifetimeErrorHandler( Class<?> mortalClass, Collection<SourceLocation> allocationStackTrace )
	{
		String stackTraceText = allocationStackTrace.isEmpty() ? "Collection of allocation stack trace is not enabled for this class." : allocationStackTrace.stream().map( DevelopmentLifeGuard::sourceLocationToString ).collect( Collectors.joining() );
		Log.debug( "Mortal is still alive: " + mortalClass + "\n" + stackTraceText );
		Debug.breakPoint();
	}

	private static String sourceLocationToString( SourceLocation stackFrame )
	{
		return "\tat " + stackFrame.stringRepresentation() + "\n";
	}
}
