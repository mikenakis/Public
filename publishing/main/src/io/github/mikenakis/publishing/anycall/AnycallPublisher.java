package io.github.mikenakis.publishing.anycall;

import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.intertwine.Anycall;
import io.github.mikenakis.intertwine.MethodKey;
import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.kit.logging.Log;
import io.github.mikenakis.live.AbstractMortalCoherent;
import io.github.mikenakis.live.Live;
import io.github.mikenakis.live.guard.LifeGuard;
import io.github.mikenakis.tyraki.MutableCollection;
import io.github.mikenakis.tyraki.mutable.MutableCollections;

import java.util.Optional;

/**
 * Allows registering/un-registering subscribers, and a {@link #allSubscribers()} method for invoking them.
 *
 * @author michael.gr
 */
public final class AnycallPublisher<T> extends AbstractMortalCoherent
{
	public static <T> AnycallPublisher<T> of( Coherence coherence )
	{
		return new AnycallPublisher<>( coherence );
	}

	private final LifeGuard lifeGuard = LifeGuard.of( this );
	private final MutableCollection<AnycallSubscription<T>> subscriptions = MutableCollections.of( coherence() ).newIdentityLinkedHashSet(); // NOTE: monstrous heisenbug would be caused by `newIdentityHashSet()` here.

	private AnycallPublisher( Coherence coherence )
	{
		super( coherence );
	}

	@Override protected LifeGuard lifeGuard() { return lifeGuard; }

	public Live<AnycallSubscription<T>> addSubscription( Anycall<T> subscriber )
	{
		assert mustBeAliveAssertion();
		assert mustBeWritableAssertion();
		var subscription = AnycallSubscription.of( this, subscriber );
		subscriptions.add( subscription.target() );
		return subscription;
	}

	void deregisterSubscription( AnycallSubscription<T> subscription )
	{
		assert mustBeAliveAssertion();
		assert mustBeWritableAssertion();
		subscriptions.remove( subscription );
	}

	@Override protected void onClose()
	{
		if( subscriptions.nonEmpty() )
		{
			Log.warning( subscriptions.size() + "  subscriptions still open: " );
			for( AnycallSubscription<T> subscription : subscriptions )
				Log.warning( "    " + Kit.identityString( subscription ) + ": " + subscription.toString() );
			//forget them and run garbage collection so that their lifeguards will raise warnings
			subscriptions.clear();
			Kit.runGarbageCollection();
		}
		super.onClose();
	}

	public Anycall<T> allSubscribers()
	{
		assert mustBeAliveAssertion();
		assert mustBeWritableAssertion();
		return this::anycall;
	}

	private Optional<Object> anycall( MethodKey<T> key, Object[] arguments )
	{
		for( Anycall<T> subscriber : subscriptions.map( subscription -> subscription.subscriber() ).toList() )
			//Kit.trySwallow( () -> // TODO: revise the purposefulness of trySwallow() here. (Or everywhere, for that matter.)
			{
				Object result = subscriber.anycall( key, arguments );
				assert result == null;
			} //);
		return Optional.empty();
	}

	public boolean isEmpty()
	{
		assert mustBeAliveAssertion();
		assert mustBeReadableAssertion();
		return subscriptions.isEmpty();
	}

	@Override public String toString()
	{
		return super.toString() + "; " + subscriptions.size() + " subscriptions";
	}
}
