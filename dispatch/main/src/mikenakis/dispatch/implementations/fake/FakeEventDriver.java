package mikenakis.dispatch.implementations.fake;

import mikenakis.dispatch.EventDriver;
import mikenakis.dispatch.Dispatcher;
import mikenakis.kit.functional.Function0;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;
import mikenakis.kit.ref.Ref;
import mikenakis.publishing.bespoke.Publisher;
import mikenakis.publishing.bespoke.Subscription;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

public final class FakeEventDriver extends Mutable implements EventDriver
{
	private final Publisher<Procedure0> idleEventPublisher = Publisher.of( mutationContext, Procedure0.class );
	private final Publisher<Procedure0> quitEventPublisher = Publisher.of( mutationContext, Procedure0.class );
	private final Dispatcher dispatcher = new Dispatcher()
	{
		@Override public void post( Procedure0 procedure )
		{
			queue.add( procedure );
		}

		@Override public <R> R call( Function0<R> function )
		{
			Ref<Optional<R>> resultRef = Ref.of( Optional.empty() );
			queue.add( () -> resultRef.value = Optional.of( function.invoke() ) );
			processEventBurst();
			return resultRef.value.orElseThrow();
		}
	};
	private final Queue<Procedure0> queue = new LinkedList<>();
	private boolean running;

	private FakeEventDriver( MutationContext mutationContext )
	{
		super( mutationContext );
		running = true;
	}

	public void processEventBurst()
	{
		assert running;
		while( !queue.isEmpty() )
		{
			Procedure0 procedure = queue.remove();
			procedure.invoke();
		}
		idleEventPublisher.allSubscribers().invoke();
	}

	@Override public Dispatcher dispatcher()
	{
		return dispatcher;
	}

	@Override public Subscription<Procedure0> newQuitEventSubscription( Procedure0 subscriber )
	{
		return quitEventPublisher.addSubscription( subscriber );
	}

	@Override public Subscription<Procedure0> newIdleEventSubscription( Procedure0 subscriber )
	{
		return idleEventPublisher.addSubscription( subscriber );
	}

	@Override public void quit()
	{
		dispatcher().post( () -> running = false );
	}

	@Override public boolean isRunning()
	{
		return running;
	}
}
