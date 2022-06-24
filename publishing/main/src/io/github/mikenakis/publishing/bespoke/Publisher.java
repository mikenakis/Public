package io.github.mikenakis.publishing.bespoke;

import io.github.mikenakis.coherence.Coherence;
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
public final class Publisher<T> extends AbstractMortalCoherent
{
	public static <T> Publisher<T> of( Coherence coherence, Class<T> interfaceType )
	{
		return new Publisher<>( coherence, interfaceType );
	}

	private final LifeGuard lifeGuard = LifeGuard.of( this, true );
	@Override protected LifeGuard lifeGuard() { return lifeGuard; }
	private final Class<T> interfaceType;
	private final T entwiner;
	private final AnycallPublisher<T> anycallPublisher = AnycallPublisher.of( coherence );

	private Publisher( Coherence coherence, Class<T> interfaceType )
	{
		super( coherence );
		this.interfaceType = interfaceType;
		entwiner = IntertwineFactory.instance.getIntertwine( interfaceType ).newEntwiner( anycallPublisher.allSubscribers() );
	}

	public Live<Subscription<T>> addSubscription( T subscriber )
	{
		assert mustBeWritableAssertion();
		Anycall<T> untwiner = IntertwineFactory.instance.getIntertwine( interfaceType ).newUntwiner( subscriber );
		Live<AnycallSubscription<T>> anycallSubscription = anycallPublisher.addSubscription( untwiner );
		return Subscription.of( this, subscriber, anycallSubscription );
	}

	public Live<AnycallSubscription<T>> addAnycallSubscription( Anycall<T> subscriber )
	{
		return anycallPublisher.addSubscription( subscriber );
	}

	@Override protected void onClose()
	{
		anycallPublisher.close();
		super.onClose();
	}

	public T allSubscribers()
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
