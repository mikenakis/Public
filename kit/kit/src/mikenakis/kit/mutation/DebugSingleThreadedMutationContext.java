package mikenakis.kit.mutation;

/**
 * Debug Single-threaded {@link MutationContext}.
 *
 * @author michael.gr
 */
final class DebugSingleThreadedMutationContext implements MutationContext
{
	private static final ThreadLocal<DebugSingleThreadedMutationContext> instance = ThreadLocal.withInitial( DebugSingleThreadedMutationContext::new );

	static DebugSingleThreadedMutationContext instance()
	{
		return instance.get();
	}

	private final Thread constructionThread;

	private DebugSingleThreadedMutationContext()
	{
		constructionThread = Thread.currentThread();
	}

	@Override public boolean inContextAssertion()
	{
		assert Thread.currentThread() == constructionThread;
		return true;
	}

	@Override public String toString()
	{
		return Thread.currentThread() == constructionThread ? "entered" : "not entered";
	}
}
