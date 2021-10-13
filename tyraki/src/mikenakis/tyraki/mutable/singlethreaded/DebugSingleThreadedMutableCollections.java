package mikenakis.tyraki.mutable.singlethreaded;

import mikenakis.tyraki.mutable.MutableCollections;

/**
 * Debug Single-threaded {@link MutableCollections}.
 *
 * @author michael.gr
 */
public final class DebugSingleThreadedMutableCollections extends MutableCollections
{
	private static final ThreadLocal<MutableCollections> instance = ThreadLocal.withInitial( () -> new DebugSingleThreadedMutableCollections() );

	public static MutableCollections instance()
	{
		return instance.get();
	}

	private final Thread currentThread;

	private DebugSingleThreadedMutableCollections()
	{
		currentThread = Thread.currentThread();
	}

	@Override public boolean assertCoherence()
	{
		assert Thread.currentThread() == currentThread;
		return true;
	}
}
