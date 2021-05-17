package mikenakis.kit.events.fake;

import mikenakis.kit.events.EventDriver;
import mikenakis.kit.events.OneOffTimer;
import mikenakis.kit.Kit;
import mikenakis.kit.functional.Procedure0;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public final class FakeEventDriver extends EventDriver
{
	/**
	 * A Blocking Queue which supports wait-until-non-empty.
	 * PEARL: the JDK's {@link BlockingQueue} interface does not offer any means of waiting while the queue is empty.
	 * Presumably they do this because they only envision multiple-consumer scenarios, where the queue may momentarily become non-empty and then
	 * immediately empty again, before the consumer that was waiting has had a chance to read an element.
	 * However, here we have only one consumer, so wait-until-non-empty makes sense.
	 */
	private static final class Queue<T>
	{
		private final BlockingQueue<T> jdkQueue = new LinkedBlockingQueue<>();
		private T savedItem = null;

		Queue()
		{
		}

		boolean isEmpty()
		{
			if( savedItem != null )
				return false;
			return jdkQueue.isEmpty();
		}

		void waitWhileEmpty()
		{
			if( savedItem != null )
				return;
			savedItem = Kit.unchecked( () -> jdkQueue.take() );
		}

		T remove()
		{
			if( savedItem != null )
			{
				T result = savedItem;
				savedItem = null;
				return result;
			}
			return Kit.unchecked( () -> jdkQueue.take() );
		}

		void add( T item )
		{
			jdkQueue.add( item );
		}
	}

	private final Queue<Procedure0> queue = new Queue<>();
	private volatile boolean running;
	//public           TimeZoneInfo               TimeZone { get; set; }
	private final SortedSet<FakeOneOffTimer> timers = new TreeSet<>();
	private final EventDriver.Proxy proxy = new EventDriver.Proxy( this::post, Duration.ofMillis( Long.MAX_VALUE ) );
	private final FakeClock clock = new FakeClock( ZoneOffset.UTC, Kit.time.createInstant( 2020, 1, 1, 1, 0, 0, 0 ) );

	public FakeEventDriver()
	{
	}

	public void run()
	{
		assert threadGuard.inThreadAssertion();
		assert !running;
		for( running = true; running; )
		{
			if( !processMessageBurst() )
				queue.waitWhileEmpty();
		}
		quitEventMulticaster.invokeAndClear();
	}

	public void setCurrentTime( Instant value )
	{
		assert value.isAfter( clock.currentTime );
		clock.currentTime = value;
		advance();
	}

	public void advance()
	{
		for( ; ; )
		{
			if( timers.isEmpty() )
				break;
			FakeOneOffTimer timer = timers.first();
			if( clock.currentTime.isBefore( timer.timeToFire ) )
				break;
			timer.fire();
			assert !timers.contains( timer ); //the timer should have called us to remove it.
		}
	}

	public boolean processMessageBurst() //returns true if any messages were processed; false if there were no messages.
	{
		assert threadGuard.inThreadAssertion();
		if( !processSingleMessage() )
		{
			idleEventMulticaster.invokeAndClear();
			return false;
		}
		for( ; ; )
			if( !processSingleMessage() )
				break;
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

	//public override TimeZoneInfo GetLocalTimeZone() => TimeZone;

	@Override public OneOffTimer newOneOffTimer( Duration delay )
	{
		Instant timeToFire = clock.currentTime.plus( delay );
		var timer = new FakeOneOffTimer( this, timeToFire );
		Kit.collection.add( timers, timer );
		return timer;
	}

	private void post( Procedure0 procedure )
	{
		assert threadGuard.outOfThreadAssertion();
		queue.add( () -> //
		{
			assert threadGuard.inThreadAssertion();
			procedure.invoke();
		} );
	}

	private boolean processSingleMessage() //returns true if a message was processed; false if there was no message.
	{
		assert threadGuard.inThreadAssertion();
		if( queue.isEmpty() )
			return false;
		Procedure0 event = queue.remove();
		event.invoke();
		return true;
	}

	//var    timeSpan    = new TimeSpan( -5, 0, 0 );
	//String displayName = DotNetHelpers.MakeTimeZoneDisplayName( timeSpan );
	//TimeZone = TimeZoneInfo.CreateCustomTimeZone( "Standard Testing Time", timeSpan, $"({displayName}) Testing Time (Testing Only)", "Standard Testing Time" );

	void remove( FakeOneOffTimer timer )
	{
		Kit.collection.remove( timers, timer );
	}
}
