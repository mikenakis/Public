package mikenakis.publishing.anycall;

import mikenakis.coherence.AbstractCoherent;
import mikenakis.intertwine.Anycall;
import mikenakis.lifetime.Mortal;
import mikenakis.lifetime.guard.LifeGuard;

/**
 * Represents a registration of a subscriber to an {@link AnycallPublisher}.
 *
 * @author michael.gr
 */
public class AnycallSubscription<T> extends AbstractCoherent implements Mortal.Defaults
{
	private final LifeGuard lifeGuard = LifeGuard.of( this, true );
	private final AnycallPublisher<T> publisher;
	final Anycall<T> subscriber;

	AnycallSubscription( AnycallPublisher<T> publisher, Anycall<T> subscriber )
	{
		super( publisher.coherence() );
		this.publisher = publisher;
		this.subscriber = subscriber;
	}

	protected void onClose()
	{
		publisher.deregisterSubscription( this );
	}

	@Override public boolean mustBeAliveAssertion()
	{
		assert mustBeReadableAssertion();
		return lifeGuard.mustBeAliveAssertion();
	}

	@Override public final void close()
	{
		assert mustBeAliveAssertion();
		assert mustBeWritableAssertion();
		onClose();
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
}
