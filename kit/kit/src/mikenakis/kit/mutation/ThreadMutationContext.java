package mikenakis.kit.mutation;

/**
 * Thread {@link MutationContext}.
 *
 * @author michael.gr
 */
final class ThreadMutationContext extends SimpleMutationContext
{
	private final Thread constructionThread;

	ThreadMutationContext()
	{
		constructionThread = Thread.currentThread();
	}

	@Override protected boolean isEntered()
	{
		return Thread.currentThread() == constructionThread;
	}
}
