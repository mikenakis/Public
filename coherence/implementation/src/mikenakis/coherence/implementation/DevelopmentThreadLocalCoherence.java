package mikenakis.coherence.implementation;

import mikenakis.coherence.Coherence;

/**
 * Debug Single-threaded {@link Coherence}.
 *
 * @author michael.gr
 */
final class DevelopmentThreadLocalCoherence
{
	private static final ThreadLocal<Coherence> instance = ThreadLocal.withInitial( ThreadCoherence::new );

	static Coherence instance()
	{
		return instance.get();
	}
}
