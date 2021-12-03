package mikenakis.kit.mutation;

/**
 * Debug Single-threaded {@link MutationContext}.
 *
 * @author michael.gr
 */
final class DebugSingleThreadedMutationContext implements MutationContext
{
	private static final ThreadLocal<MutationContext> instance = ThreadLocal.withInitial( () -> new DebugSingleThreadedMutationContext() );

	public static MutationContext instance()
	{
		return instance.get();
	}

	private final Thread currentThread;

	private DebugSingleThreadedMutationContext()
	{
		currentThread = Thread.currentThread();
	}

	@Override public boolean inContextAssertion()
	{
		assert Thread.currentThread() == currentThread;
		return true;
	}
}
