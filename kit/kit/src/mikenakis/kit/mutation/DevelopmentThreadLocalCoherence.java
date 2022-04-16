package mikenakis.kit.mutation;

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
