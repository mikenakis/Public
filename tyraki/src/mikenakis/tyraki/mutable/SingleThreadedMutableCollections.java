package mikenakis.tyraki.mutable;

import mikenakis.kit.mutation.SingleThreadedMutationContext;

/**
 * Single-threaded {@link MutableCollections}.
 *
 * @author michael.gr
 */
public final class SingleThreadedMutableCollections
{
	private static final ThreadLocal<MutableCollections> instance = ThreadLocal.withInitial( () -> //
		new MutableCollections( SingleThreadedMutationContext.instance() ) );

	public static MutableCollections instance()
	{
		return instance.get();
	}
}
