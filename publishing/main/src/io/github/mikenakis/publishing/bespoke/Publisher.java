package io.github.mikenakis.publishing.bespoke;

import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.coherence.Coherent;
import io.github.mikenakis.intertwine.Anycall;
import io.github.mikenakis.intertwine.IntertwineFactory;
import io.github.mikenakis.live.AbstractMortalCoherent;
import io.github.mikenakis.live.Live;
import io.github.mikenakis.live.guard.LifeGuard;
import io.github.mikenakis.publishing.anycall.AnycallPublisher;
import io.github.mikenakis.publishing.anycall.AnycallSubscription;

/**
 * Allows registering/un-registering of subscribers, and offers a {@link #allSubscribers()} method for invoking them all.
 *
 * @author michael.gr
 */
public interface Publisher<T> extends Coherent
{
	static <T> Live<Publisher<T>> of( Coherence coherence, Class<T> interfaceType )
	{
		final class Implementation extends AbstractMortalCoherent implements Publisher<T>
		{
			private final LifeGuard lifeGuard = LifeGuard.of( this, true );
			@Override protected LifeGuard lifeGuard() { return lifeGuard; }
			private final T entwiner;
			private final AnycallPublisher<T> anycallPublisher = AnycallPublisher.of( coherence );

			private Implementation()
			{
				super( coherence );
				entwiner = IntertwineFactory.instance.getIntertwine( interfaceType ).newEntwiner( anycallPublisher.allSubscribers() );
			}

			@Override public Live<Subscription<T>> addSubscription( T subscriber )
			{
				assert mustBeWritableAssertion();
				Anycall<T> untwiner = IntertwineFactory.instance.getIntertwine( interfaceType ).newUntwiner( subscriber );
				Live<AnycallSubscription<T>> anycallSubscription = anycallPublisher.addSubscription( untwiner );
				return Subscription.of( this, subscriber, anycallSubscription );
			}

			@Override public Live<AnycallSubscription<T>> addAnycallSubscription( Anycall<T> subscriber )
			{
				return anycallPublisher.addSubscription( subscriber );
			}

			@Override protected void onClose()
			{
				anycallPublisher.close();
				super.onClose();
			}

			@Override public T allSubscribers()
			{
				assert mustBeReadableAssertion();
				return entwiner;
			}

			public boolean isEmpty()
			{
				return anycallPublisher.isEmpty();
			}

			@Override public String toString()
			{
				return interfaceType + "; " + anycallPublisher.toString();
			}
		}

		var result = new Implementation();
		return Live.of( result, result::close );
	}

	Live<Subscription<T>> addSubscription( T subscriber );
	T allSubscribers();
	Live<AnycallSubscription<T>> addAnycallSubscription( Anycall<T> subscriber );
}
