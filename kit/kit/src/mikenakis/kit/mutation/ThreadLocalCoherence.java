package mikenakis.kit.mutation;

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
		return UnknownCoherence.instance;
	}

	private ThreadLocalCoherence()
	{
	}
}
