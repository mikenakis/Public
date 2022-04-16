package mikenakis.publishing.anycall;

import mikenakis.intertwine.Anycall;
import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.kit.mutation.AbstractCoherent;

/**
 * Represents a registration of a subscriber to an {@link AnycallPublisher}.
 *
 * @author michael.gr
 */
public class AnycallSubscription<T> extends AbstractCoherent implements Closeable.Defaults
{
	private final LifeGuard lifeGuard = LifeGuard.of( this );
	private final AnycallPublisher<T> publisher;
	final Anycall<T> subscriber;

	AnycallSubscription( AnycallPublisher<T> publisher, Anycall<T> subscriber )
	{
		super( publisher.coherence() );
		this.publisher = publisher;
		this.subscriber = subscriber;
	}

	@Override public boolean mustBeAliveAssertion()
	{
		assert mustBeReadableAssertion();
		return lifeGuard.mustBeAliveAssertion();
	}

	@Override public void close()
	{
		assert mustBeAliveAssertion();
		assert mustBeWritableAssertion();
		publisher.deregisterSubscription( this );
		lifeGuard.close();
	}
}
