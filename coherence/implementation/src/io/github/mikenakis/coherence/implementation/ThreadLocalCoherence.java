package io.github.mikenakis.coherence.implementation;

import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.coherence.NullaryCoherence;
import io.github.mikenakis.kit.Kit;

/**
 * Thread-local {@link Coherence}.
 *
 * @author michael.gr
 */
public final class ThreadLocalCoherence
{
	public static Coherence instance()
	{
		if( Kit.areAssertionsEnabled() )
			return DevelopmentThreadLocalCoherence.instance();
		return NullaryCoherence.instance;
	}

	private ThreadLocalCoherence()
	{
	}
}
