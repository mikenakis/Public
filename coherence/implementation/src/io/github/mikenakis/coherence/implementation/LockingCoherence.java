package io.github.mikenakis.coherence.implementation;

import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.debug.Debug;
import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.kit.functional.Function0;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Locking {@link Coherence}.
 *
 * @author michael.gr
 */
public final class LockingCoherence extends SimpleCoherence
{
	private final ReentrantLock lock = new ReentrantLock();

	public LockingCoherence()
	{
	}

	public <T> T with( Function0<T> function0 )
	{
		Kit.unchecked( () -> lock.lockInterruptibly() );
		try
		{
			return Debug.boundary( () -> function0.invoke() );
		}
		finally
		{
			lock.unlock();
		}
	}

	@Override protected boolean isEntered()
	{
		return lock.isLocked();
	}

	@Override public String toString()
	{
		return "isEntered: " + isEntered();
	}
}
