package mikenakis.kit.coherence;

/**
 * Thread {@link Coherence}.
 *
 * @author michael.gr
 */
final class ThreadCoherence extends SimpleCoherence
{
	private final Thread constructionThread;

	ThreadCoherence()
	{
		constructionThread = Thread.currentThread();
	}

	@Override protected boolean isEntered()
	{
		return Thread.currentThread() == constructionThread;
	}
}
