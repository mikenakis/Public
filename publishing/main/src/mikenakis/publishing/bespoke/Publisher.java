package mikenakis.publishing.bespoke;

import mikenakis.intertwine.Anycall;
import mikenakis.intertwine.IntertwineFactory;
import mikenakis.kit.lifetime.AbstractMortalCoherent;
import mikenakis.kit.mutation.Coherence;
import mikenakis.publishing.anycall.AnycallPublisher;
import mikenakis.publishing.anycall.AnycallSubscription;

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

	private final Class<T> interfaceType;
	private final T entwiner;
	private final AnycallPublisher<T> anycallPublisher = AnycallPublisher.of( coherence );

	private Publisher( Coherence coherence, Class<T> interfaceType )
	{
		super( coherence );
		this.interfaceType = interfaceType;
		entwiner = IntertwineFactory.instance.getIntertwine( interfaceType ).newEntwiner( anycallPublisher.allSubscribers() );
	}

	public Subscription<T> addSubscription( T subscriber )
	{
		assert mustBeWritableAssertion();
		Anycall<T> untwiner = IntertwineFactory.instance.getIntertwine( interfaceType ).newUntwiner( subscriber );
		AnycallSubscription<T> anycallSubscription = anycallPublisher.addSubscription( untwiner );
		return new Subscription<>( this, subscriber, anycallSubscription );
	}

	public AnycallSubscription<T> addAnycallSubscription( Anycall<T> subscriber )
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
