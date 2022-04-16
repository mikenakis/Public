package mikenakis.publishing.bespoke;

import mikenakis.kit.lifetime.AbstractMortalCoherent;
import mikenakis.publishing.anycall.AnycallSubscription;

/**
 * Represents a registration of a subscriber to a {@link Publisher}.
 *
 * @author michael.gr
 */
public class Subscription<T> extends AbstractMortalCoherent
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

	@Override protected void onClose()
	{
		anycallSubscription.close();
		super.onClose();
	}

	@Override public String toString()
	{
		return super.toString() + " publisher: " + publisher + " subscriber: " + subscriber;
	}
}
