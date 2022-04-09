package mikenakis.kit.coherence;

import mikenakis.kit.Kit;
import mikenakis.kit.functional.Function0;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.mutation.MutationContext;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Synchronized {@link Coherence}.
 *
 * @author michael.gr
 */
public class SynchronizedCoherence implements Coherence.Defaults
{
	private final MutationContext mutationContext = new MutationContext()
	{
		@Override public boolean inContextAssertion()
		{
			assert assertCoherence();
			return true;
		}

		@Override public boolean isFrozen()
		{
			return false;
		}
	};

	private final ReentrantLock lock = new ReentrantLock();
	private volatile boolean entered;

	public SynchronizedCoherence()
	{
	}

	@Override public MutationContext mutationContext()
	{
		return mutationContext;
	}

	@Override public <R> R cohere( Function0<R> function )
	{
		return Kit.sync.lock( lock, () ->
		{
			if( entered )
				return function.invoke();
			else
			{
				entered = true;
				return Kit.tryFinally( function, () -> entered = false );
			}
		} );
	}

	@Override public void cohere( Procedure0 procedure )
	{
		Kit.sync.lock( lock, () ->
		{
			if( entered )
				procedure.invoke();
			else
			{
				entered = true;
				Kit.tryFinally( procedure, () -> entered = false );
			}
		} );
	}

	@Override public boolean assertCoherence()
	{
		assert isEntered();
		return true;
	}

	private boolean isEntered()
	{
		assert entered == lock.isHeldByCurrentThread();
		return !lock.isLocked() || entered;
	}

	@Override public String toString()
	{
		return "locked: " + lock.isLocked() + "; held-by-current-thread: " + lock.isHeldByCurrentThread() + "; entered: " + entered;
	}
}
