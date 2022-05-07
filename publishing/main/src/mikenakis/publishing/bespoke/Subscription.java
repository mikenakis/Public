package mikenakis.publishing.bespoke;

import mikenakis.coherence.AbstractCoherent;
import mikenakis.lifetime.Mortal;
import mikenakis.publishing.anycall.AnycallSubscription;

/**
 * Represents a registration of a subscriber to a {@link Publisher}.
 *
 * @author michael.gr
 */
public class Subscription<T> extends AbstractCoherent implements Mortal.Defaults
{
	private final Publisher<T> publisher;
	private final T subscriber;
	private final AnycallSubscription<T> anycallSubscription;

	Subscription( Publisher<T> publisher, T subscriber, AnycallSubscription<T> anycallSubscription )
	{
		super( publisher.coherence() );
		this.publisher = publisher;
		this.subscriber = subscriber;
		this.anycallSubscription = anycallSubscription;
	}

	@Override public String toString()
	{
		return super.toString() + " publisher: " + publisher + " subscriber: " + subscriber;
	}

	@Override public boolean mustBeAliveAssertion()
	{
		return anycallSubscription.mustBeAliveAssertion();
	}

	@Override public void close()
	{
		anycallSubscription.close();
	}
}
