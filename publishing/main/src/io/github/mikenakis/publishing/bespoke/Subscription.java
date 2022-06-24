package io.github.mikenakis.publishing.bespoke;

import io.github.mikenakis.coherence.AbstractCoherent;
import io.github.mikenakis.coherence.Coherent;
import io.github.mikenakis.live.Live;
import io.github.mikenakis.live.Mortal;
import io.github.mikenakis.publishing.anycall.AnycallSubscription;

/**
 * Represents a registration of a subscriber to a {@link Publisher}.
 *
 * @author michael.gr
 */
public interface Subscription<T> extends Coherent
{
	static <T> Live<Subscription<T>> of( Publisher<T> publisher, T subscriber, Live<AnycallSubscription<T>> anycallSubscription )
	{
		class Implementation extends AbstractCoherent implements Subscription<T>, Mortal.Defaults
		{
			private final Publisher<T> publisher;
			private final T subscriber;
			private final Live<AnycallSubscription<T>> anycallSubscription;

			private Implementation( Publisher<T> publisher, T subscriber, Live<AnycallSubscription<T>> anycallSubscription )
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

		var result = new Implementation( publisher, subscriber, anycallSubscription );
		return Live.of( result, result::close );
	}
}
