package mikenakis.tyraki.mutable;

import mikenakis.kit.mutation.ThreadLocalMutationContext;

/**
 * Single-threaded {@link MutableCollections}.
 *
 * @author michael.gr
 */
public final class SingleThreadedMutableCollections
{
	private static final ThreadLocal<MutableCollections> instance = ThreadLocal.withInitial( () -> //
		MutableCollections.of( ThreadLocalMutationContext.instance() ) );

	public static MutableCollections instance()
	{
		return instance.get();
	}
}
