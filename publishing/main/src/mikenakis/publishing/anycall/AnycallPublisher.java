package mikenakis.publishing.anycall;

import mikenakis.intertwine.Anycall;
import mikenakis.intertwine.MethodKey;
import mikenakis.kit.Kit;
import mikenakis.kit.coherence.Coherence;
import mikenakis.kit.lifetime.AbstractMortalCoherent;
import mikenakis.kit.logging.Log;
import mikenakis.tyraki.MutableCollection;
import mikenakis.tyraki.mutable.MutableCollections;

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

	private final MutableCollection<AnycallSubscription<T>> subscriptions = MutableCollections.of( coherence ).newIdentityLinkedHashSet(); // NOTE: monstrous heisenbug would be caused by `newIdentityHashSet()` here.

	private AnycallPublisher( Coherence coherence )
	{
		super( coherence );
	}

	public AnycallSubscription<T> addSubscription( Anycall<T> subscriber )
	{
		assert mustBeAliveAssertion();
		assert mustBeWritableAssertion();
		var subscription = new AnycallSubscription<>( this, subscriber );
		subscriptions.add( subscription );
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
			for( var subscription : subscriptions )
				Log.warning( "    " + subscription );
			subscriptions.clear(); //forget them so that their lifeguards will raise warnings
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
		for( Anycall<T> subscriber : subscriptions.map( subscription -> subscription.subscriber ).toList() )
			Kit.trySwallow( () -> //
			{
				Object result = subscriber.anycall( key, arguments );
				assert result == null;
			} );
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
