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
			private Implementation()
			{
				super( publisher.coherence() );
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

		var result = new Implementation();
		return Live.of( result, result::close );
	}
}
