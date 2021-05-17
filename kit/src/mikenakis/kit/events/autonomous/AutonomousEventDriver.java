package mikenakis.kit.events.autonomous;

import mikenakis.kit.events.EventDriver;
import mikenakis.kit.events.OneOffTimer;
import mikenakis.kit.Kit;
import mikenakis.kit.functional.Procedure0;

import java.time.Clock;
import java.time.Duration;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

public final class AutonomousEventDriver extends EventDriver
{
	private final SingleConsumerBlockingQueue<Procedure0> queue = new SingleConsumerBlockingQueue<>();
	private volatile boolean running;
	private final SortedSet<AutonomousOneOffTimer> timers = new TreeSet<>();
	private final EventDriver.Proxy proxy = new EventDriver.Proxy( this::post, Duration.ofMillis( Long.MAX_VALUE ) );
	private final TickClock clock;

	public AutonomousEventDriver( Clock clock )
	{
		this.clock = new TickClock( clock );
	}

	public void run()
	{
		assert threadGuard.inThreadAssertion();
		assert !running;
		for( running = true; running; )
		{
			if( !processEventBurst() )
				waitForNextEvent();
		}
		quitEventMulticaster.invokeAndClear();
	}

	/**
	 * Processes an event burst.
	 * Returns true if any events were processed. (This includes any timers fired.)
	 * If no events were processed, then idle is invoked and false is returned.
	 */
	public boolean processEventBurst()
	{
		assert threadGuard.inThreadAssertion();
		if( !processPartialEventBurst() )
		{
			idle();
			return false;
		}
		for( ; ; ) //process multiple partial event bursts
		{
			if( !processPartialEventBurst() )
				break;
		}
		return true;
	}

	@Override public Proxy proxy()
	{
		return proxy;
	}

	@Override public void postQuit()
	{
		queue.add( () -> running = false );
	}

	@Override public boolean isRunning()
	{
		return running;
	}

	@Override public Clock clock()
	{
		return clock;
	}

	@Override public OneOffTimer newOneOffTimer( Duration delay )
	{
		AutonomousOneOffTimer autonomousOneOffTimer = new AutonomousOneOffTimer( clock.currentTime.plus( delay ), this::removeTimer );
		Kit.collection.add( timers, autonomousOneOffTimer );
		return autonomousOneOffTimer;
	}

	private void removeTimer( AutonomousOneOffTimer timer )
	{
		Kit.collection.remove( timers, timer );
	}

	private void waitForNextEvent()
	{
		Duration timeout = durationToNextTimer();
		queue.waitUntilNonEmptyOrTimeout( timeout );
	}

	private AutonomousOneOffTimer nextTimer()
	{
		if( timers.isEmpty() )
			return null;
		return timers.first();
	}

	private Duration durationToNextTimer()
	{
		AutonomousOneOffTimer nextTimer = nextTimer();
		if( nextTimer == null )
			return null;
		return Duration.between( clock.currentTime, nextTimer.timeToFire );
	}

	private boolean processPartialEventBurst() //returns true if any events were processed; false otherwise.
	{
		clock.tick(); // the current time is obtained once at the beginning of each partial event burst and remains the same during the partial event burst.
		clock.currentTime = clock.instant(); //the current time is obtained once at the beginning of each partial event burst.
		boolean b = processTimers(); //timers are processed once per partial burst, as if they are posting events from a separate thread, rather than on idle.
		Collection<Procedure0> events = queue.extractAllElements();
		if( events.isEmpty() )
			return b;
		for( Procedure0 procedure : events )
			procedure.invoke();
		return true;
	}

	private void idle()
	{
		processTimers();
		idleEventMulticaster.invokeAndClear();
	}

	private boolean processTimers() //returns true if any timers were fired; false if there were no timers to fire.
	{
		boolean any = false;
		for( ; ; )
		{
			AutonomousOneOffTimer nextTimer = nextTimer();
			if( nextTimer == null )
				break;
			if( clock.currentTime.isBefore( nextTimer.timeToFire ) )
				break;
			nextTimer.fire();
			assert !timers.contains( nextTimer ); //it should have removed itself.
			any = true;
		}
		return any;
	}

	private void post( Procedure0 procedure )
	{
		assert threadGuard.outOfThreadAssertion();
		queue.add( () -> {
			assert threadGuard.inThreadAssertion();
			procedure.invoke();
		} );
	}
}
