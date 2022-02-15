package mikenakis.kit.coherence;

import mikenakis.kit.Kit;

/**
 * Single-threaded {@link Coherence}.
 *
 * @author michael.gr
 */
public final class SingleThreadedCoherence
{
	public static Coherence instance()
	{
		if( Kit.areAssertionsEnabled() )
			return DebugSingleThreadedCoherence.instance();
		return ReleaseSingleThreadedCoherence.instance;
	}

	private SingleThreadedCoherence()
	{
	}
}
