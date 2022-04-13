package mikenakis.publishing.bespoke;

import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.kit.mutation.Mutable;
import mikenakis.publishing.anycall.AnycallSubscription;

/**
 * Represents a registration of a subscriber to a {@link Publisher}.
 *
 * @author michael.gr
 */
public class Subscription<T> extends Mutable implements Closeable.Defaults
{
	private final LifeGuard lifeGuard = LifeGuard.of( this );
	private final Publisher<T> publisher;
	private final T subscriber;
	private final AnycallSubscription<T> anycallSubscription;

	Subscription( Publisher<T> publisher, T subscriber, AnycallSubscription<T> anycallSubscription )
	{
		super( publisher.mutationContext() );
		this.publisher = publisher;
		this.subscriber = subscriber;
		this.anycallSubscription = anycallSubscription;
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
		anycallSubscription.close();
		lifeGuard.close();
	}

	@Override public String toString()
	{
		return lifeGuard.toString() + " publisher: " + publisher + " subscriber: " + subscriber;
	}
}
