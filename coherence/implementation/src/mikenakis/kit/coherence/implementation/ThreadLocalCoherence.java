package mikenakis.kit.coherence.implementation;

import mikenakis.kit.Kit;
import mikenakis.kit.coherence.Coherence;
import mikenakis.kit.coherence.UnknownCoherence;

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
		return UnknownCoherence.instance;
	}

	private ThreadLocalCoherence()
	{
	}
}
