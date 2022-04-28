package mikenakis.kit.coherence.implementation;

import mikenakis.kit.coherence.Coherence;

/**
 * Debug Single-threaded {@link Coherence}.
 *
 * @author michael.gr
 */
final class DevelopmentThreadLocalCoherence
{
	private static final ThreadLocal<ThreadCoherence> instance = ThreadLocal.withInitial( ThreadCoherence::new );

	static Coherence instance()
	{
		return instance.get();
	}
}
