package mikenakis.publishing.anycall;

import mikenakis.intertwine.Anycall;
import mikenakis.kit.lifetime.AbstractMortalCoherent;

/**
 * Represents a registration of a subscriber to an {@link AnycallPublisher}.
 *
 * @author michael.gr
 */
public class AnycallSubscription<T> extends AbstractMortalCoherent
{
	private final AnycallPublisher<T> publisher;
	final Anycall<T> subscriber;

	AnycallSubscription( AnycallPublisher<T> publisher, Anycall<T> subscriber )
	{
		super( publisher.coherence() );
		this.publisher = publisher;
		this.subscriber = subscriber;
	}

	@Override protected void onClose()
	{
		publisher.deregisterSubscription( this );
		super.onClose();
	}
}
