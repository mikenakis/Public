package mikenakis.publishing.suppress;

import mikenakis.intertwine.Anycall;
import mikenakis.intertwine.Intertwine;
import mikenakis.intertwine.IntertwineFactory;
import mikenakis.intertwine.MethodKey;
import mikenakis.kit.coherence.Coherence;
import mikenakis.tyraki.MutableCollection;
import mikenakis.tyraki.UnmodifiableCollection;
import mikenakis.tyraki.mutable.MutableCollections;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Optional;

/**
 * A call suppressor.
 *
 * @author michael.gr
 */
public class CallSuppressor<N> extends Suppressor
{
	private static class QueueEntry<N>
	{
		final MethodKey<N> key;
		final Object[] args;

		QueueEntry( MethodKey<N> key, Object[] args )
		{
			this.key = key;
			this.args = args;
		}
	}

	private final N delegee;
	private final N entwiner;
	private final Anycall<N> untwiner;
	private N entryPoint;
	private Optional<MutableCollection<QueueEntry<N>>> queue = Optional.empty();

	public CallSuppressor( Coherence coherence, IntertwineFactory intertwineFactory, Class<? super N> interfaceType, N delegee )
	{
		super( coherence );
		this.delegee = delegee;
		Intertwine<N> intertwine = intertwineFactory.getIntertwine( interfaceType );
		entwiner = intertwine.newEntwiner( ( k, a ) -> //
		{
			queue.orElseThrow().add( new QueueEntry<>( k, a ) );
			return Optional.empty();
		} );
		untwiner = intertwine.newUntwiner( delegee );
		entryPoint = delegee;
	}

	@OverridingMethodsMustInvokeSuper
	@Override protected void onSuppress()
	{
		assert queue.isEmpty();
		queue = Optional.of( MutableCollections.of( coherence ).newArrayList() );
		entryPoint = entwiner;
	}

	public N getEntryPoint()
	{
		return entryPoint;
	}

	@Override @OverridingMethodsMustInvokeSuper
	protected void onRelease()
	{
		assert queue.isPresent();
		assert entryPoint == entwiner;
		UnmodifiableCollection<QueueEntry<N>> tempQueue = queue.get();
		queue = Optional.empty();
		entryPoint = delegee;
		for( QueueEntry<N> queueEntry : tempQueue )
		{
			Object result = untwiner.anycall( queueEntry.key, queueEntry.args );
			assert result == null;
		}
	}
}
