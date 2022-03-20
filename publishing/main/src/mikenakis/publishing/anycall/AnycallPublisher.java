package mikenakis.publishing.anycall;

import mikenakis.intertwine.Anycall;
import mikenakis.intertwine.MethodKey;
import mikenakis.kit.Kit;
import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.kit.logging.Log;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;
import mikenakis.tyraki.MutableCollection;
import mikenakis.tyraki.mutable.MutableCollections;

import java.util.Optional;

/**
 * Allows registering/un-registering subscribers, and a {@link #allSubscribers()} method for invoking them.
 *
 * @author michael.gr
 */
public final class AnycallPublisher<T> extends Mutable implements Closeable.Defaults
{
	public static <T> AnycallPublisher<T> of( MutationContext mutationContext )
	{
		return new AnycallPublisher<>( mutationContext );
	}

	private final LifeGuard lifeGuard = LifeGuard.of( this, true );
	private final MutableCollection<AnycallSubscription<T>> subscriptions = MutableCollections.of( mutationContext ).newIdentityLinkedHashSet(); // NOTE: monstrous heisenbug would be caused by `newIdentityHashSet()` here.

	private AnycallPublisher( MutationContext mutationContext )
	{
		super( mutationContext );
	}

	public AnycallSubscription<T> addSubscription( Anycall<T> subscriber )
	{
		assert isAliveAssertion();
		assert canMutateAssertion();
		var subscription = new AnycallSubscription<>( this, subscriber );
		subscriptions.add( subscription );
		return subscription;
	}

	void deregisterSubscription( AnycallSubscription<T> subscription )
	{
		assert isAliveAssertion();
		assert canMutateAssertion();
		subscriptions.remove( subscription );
	}

	@Override public void close()
	{
		assert isAliveAssertion();
		assert canMutateAssertion();
		if( subscriptions.nonEmpty() )
		{
			Log.warning( subscriptions.size() + "  subscriptions still open: " );
			for( var subscription : subscriptions )
				Log.warning( "    " + subscription );
			subscriptions.clear(); //forget them so that their lifeguards will raise warnings
		}
		lifeGuard.close();
	}

	@Override public boolean isAliveAssertion()
	{
		assert canReadAssertion();
		assert lifeGuard.isAliveAssertion();
		return true;
	}

	public Anycall<T> allSubscribers()
	{
		assert isAliveAssertion();
		assert canMutateAssertion();
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
		assert isAliveAssertion();
		return subscriptions.isEmpty();
	}

	@Override public String toString()
	{
		return lifeGuard.toString() + "; " + subscriptions.size() + " subscriptions";
	}
}
