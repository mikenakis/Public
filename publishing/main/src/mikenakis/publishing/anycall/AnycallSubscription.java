package mikenakis.publishing.anycall;

import mikenakis.intertwine.Anycall;
import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.kit.mutation.Mutable;

/**
 * Represents a registration of a subscriber to an {@link AnycallPublisher}.
 *
 * @author michael.gr
 */
public class AnycallSubscription<T> extends Mutable implements Closeable.Defaults
{
	private final LifeGuard lifeGuard = LifeGuard.of( this );
	private final AnycallPublisher<T> publisher;
	final Anycall<T> subscriber;

	AnycallSubscription( AnycallPublisher<T> publisher, Anycall<T> subscriber )
	{
		super( publisher.mutationContext() );
		this.publisher = publisher;
		this.subscriber = subscriber;
	}

	@Override public boolean isAliveAssertion()
	{
		assert lifeGuard.isAliveAssertion();
		return true;
	}

	@Override public void close()
	{
		assert isAliveAssertion();
		publisher.deregisterSubscription( this );
		lifeGuard.close();
	}
}
