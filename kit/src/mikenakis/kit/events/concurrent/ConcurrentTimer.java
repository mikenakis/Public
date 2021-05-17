package mikenakis.kit.events.concurrent;

import mikenakis.kit.events.EventDriver;
import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.lifetime.guard.LifeGuard;

/**
 * A timer which runs on a separate thread, thus allowing threads that are not running an {@link EventDriver} to use timers.
 * The catch is that the event handler will be invoked within a different thread, so you have to be very careful with what you do with it.
 */
// TODO: implement this class. (For now, we just use 'after()' which gives us one-off, non-revocable timers)
public class ConcurrentTimer implements Closeable.Defaults
{
	private final LifeGuard lifeGuard = LifeGuard.create( this );

	@Override public boolean lifeStateAssertion( boolean value )
	{
		assert lifeGuard.lifeStateAssertion( value );
		return true;
	}

	@Override public void close()
	{
		lifeGuard.close();
	}
}
