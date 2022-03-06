package mikenakis.kit.mutation;

/**
 * Debug Single-threaded {@link MutationContext}.
 *
 * @author michael.gr
 */
final class DebugThreadLocalMutationContext implements MutationContext
{
	private static final ThreadLocal<DebugThreadLocalMutationContext> instance = ThreadLocal.withInitial( DebugThreadLocalMutationContext::new );

	static DebugThreadLocalMutationContext instance()
	{
		return instance.get();
	}

	private final Thread constructionThread;

	private DebugThreadLocalMutationContext()
	{
		constructionThread = Thread.currentThread();
	}

	@Override public boolean isInContextAssertion()
	{
		assert Thread.currentThread() == constructionThread;
		return true;
	}

	@Override public boolean isFrozen()
	{
		return false;
	}

	@Override public String toString()
	{
		return Thread.currentThread() == constructionThread ? "entered" : "not entered";
	}
}
