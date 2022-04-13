package mikenakis.kit.mutation;

/**
 * Debug Single-threaded {@link MutationContext}.
 *
 * @author michael.gr
 */
final class DebugThreadLocalMutationContext
{
	private static final ThreadLocal<ThreadMutationContext> instance = ThreadLocal.withInitial( ThreadMutationContext::new );

	static MutationContext instance()
	{
		return instance.get();
	}
}
