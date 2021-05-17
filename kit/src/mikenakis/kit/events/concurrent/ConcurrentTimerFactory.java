package mikenakis.kit.events.concurrent;

import mikenakis.kit.Kit;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.events.autonomous.AutonomousEventDriver;
import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.lifetime.guard.LifeGuard;

import java.time.Clock;
import java.time.Duration;

/**
 * Creates instances of {@link ConcurrentTimer}.
 * <p>
 * Example usage:
 * VolatileRef<Boolean> volatileRef = new VolatileRef<>( Boolean.TRUE );
 * ParallelTimerFactory.defaultInstance().after( "MyParallelTimer", OperationType.Minor, duration, () -> volatileRef.value = Boolean.FALSE );
 * while( volatileRef.value )
 * {
 * ...do some stuff...
 * }
 */
public final class ConcurrentTimerFactory implements Closeable.Defaults
{
	private static final Object lock = new Object();
	private static ConcurrentTimerFactory defaultInstance = null;

	/**
	 * Obtains the default instance of {@link ConcurrentTimerFactory} which is hard-coded to make use of {@link Clock#systemUTC()}.
	 * If the default instance has not been created yet, it is created by this call.
	 * Once created, the default instance (and the thread it spawns internally) will stay alive for the remainder of the JVM's lifetime.
	 */
	public static ConcurrentTimerFactory defaultInstance()
	{
		synchronized( lock )
		{
			if( defaultInstance == null )
				defaultInstance = new ConcurrentTimerFactory( "default ParallelTimerFactory", Clock.systemUTC() );
			return defaultInstance;
		}
	}

	private final LifeGuard lifeGuard = LifeGuard.create( this );
	private final Clock clock;
	private final Thread thread;
	private volatile AutonomousEventDriver eventDriver;

	/**
	 * Initializes a new instance of {@link ConcurrentTimerFactory} which makes use of a supplied {@link Clock}.
	 * Potentially useful for testing {@link ConcurrentTimerFactory}.
	 *
	 * @param name the name that will be given to the thread that will be created internally.
	 */
	public ConcurrentTimerFactory( String name, Clock clock )
	{
		this.clock = clock;
		thread = new Thread( this::threadProcedure, name );
		thread.setDaemon( true );
		thread.start();
		while( eventDriver == null )
			Thread.yield();
	}

	private void threadProcedure()
	{
		eventDriver = new AutonomousEventDriver( clock );
		eventDriver.run();
	}

	@Override public boolean lifeStateAssertion( boolean value )
	{
		assert lifeGuard.lifeStateAssertion( value );
		return true;
	}

	@Override public void close()
	{
		assert lifeGuard.isAliveAssertion();
		assert thread.isAlive();
		eventDriver.postQuit();
		Kit.unchecked( () -> thread.join( 1000 ) );
		assert !thread.isAlive();
		lifeGuard.close();
	}

	public void after( Duration interval, Procedure0 handler )
	{
		eventDriver.proxy().invoke( () -> eventDriver.after( interval, handler ) );
	}
}
