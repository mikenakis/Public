package io.github.mikenakis.publishing.anycall;

import io.github.mikenakis.coherence.AbstractCoherent;
import io.github.mikenakis.coherence.Coherent;
import io.github.mikenakis.intertwine.Anycall;
import io.github.mikenakis.live.Live;
import io.github.mikenakis.live.Mortal;
import io.github.mikenakis.live.guard.LifeGuard;

/**
 * Represents a registration of a subscriber to an {@link AnycallPublisher}.
 *
 * @author michael.gr
 */
public interface AnycallSubscription<T> extends Coherent
{
	static <T> Live<AnycallSubscription<T>> of( AnycallPublisher<T> publisher, Anycall<T> subscriber )
	{
		final class Implementation<TT> extends AbstractCoherent implements AnycallSubscription<TT>, Mortal.Defaults
		{
			private final LifeGuard lifeGuard = LifeGuard.of( this, true );
			private final AnycallPublisher<TT> publisher;
			private final Anycall<TT> subscriber;

			private Implementation( AnycallPublisher<TT> publisher, Anycall<TT> subscriber )
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

			@Override protected boolean mustBeReadableAssertion()
			{
				assert lifeGuard.mustBeAliveAssertion();
				return super.mustBeReadableAssertion();
			}

			@Override protected boolean mustBeWritableAssertion()
			{
				assert lifeGuard.mustBeAliveAssertion();
				return super.mustBeWritableAssertion();
			}

			@Override public String toString()
			{
				return lifeGuard.toString();
			}

			@Override public Anycall<TT> subscriber() { return subscriber; }
		}
		var result = new Implementation<>( publisher, subscriber );
		return Live.of( result, result::close );
	}

	Anycall<T> subscriber();
}
