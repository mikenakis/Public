package mikenakis.coherence.implementation;

import mikenakis.coherence.Coherence;
import mikenakis.coherence.NullaryCoherence;
import mikenakis.kit.Kit;

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
