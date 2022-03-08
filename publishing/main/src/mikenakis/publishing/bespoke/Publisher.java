package mikenakis.publishing.bespoke;

import mikenakis.intertwine.Anycall;
import mikenakis.intertwine.IntertwineFactory;
import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;
import mikenakis.publishing.anycall.AnycallPublisher;
import mikenakis.publishing.anycall.AnycallSubscription;

/**
 * Allows registering/un-registering of subscribers, and offers a {@link #allSubscribers()} method for invoking them all.
 *
 * @author michael.gr
 */
public final class Publisher<T> extends Mutable implements Closeable.Defaults
{
	public static <T> Publisher<T> of( MutationContext mutationContext, Class<T> interfaceType )
	{
		return new Publisher<>( mutationContext, interfaceType );
	}

	private final Class<T> interfaceType;
	private final T entwiner;
	private final AnycallPublisher<T> anycallPublisher = AnycallPublisher.of( mutationContext );

	private Publisher( MutationContext mutationContext, Class<T> interfaceType )
	{
		super( mutationContext );
		this.interfaceType = interfaceType;
		entwiner = IntertwineFactory.instance.getIntertwine( interfaceType ).newEntwiner( anycallPublisher.allSubscribers() );
	}

	public Subscription<T> addSubscription( T subscriber )
	{
		assert canMutateAssertion();
		Anycall<T> untwiner = IntertwineFactory.instance.getIntertwine( interfaceType ).newUntwiner( subscriber );
		AnycallSubscription<T> anycallSubscription = anycallPublisher.addSubscription( untwiner );
		return new Subscription<>( this, subscriber, anycallSubscription );
	}

	public AnycallSubscription<T> addAnycallSubscription( Anycall<T> subscriber )
	{
		return anycallPublisher.addSubscription( subscriber );
	}

	@Override public void close()
	{
		anycallPublisher.close();
	}

	@Override public boolean isAliveAssertion()
	{
		throw new RuntimeException(); //I do not expect this to ever be called.
	}

	public T allSubscribers()
	{
		assert canMutateAssertion();
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
