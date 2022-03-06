package mikenakis.kit.mutation;

import mikenakis.kit.Kit;

/**
 * Thread-local {@link MutationContext}.
 *
 * @author michael.gr
 */
public final class ThreadLocalMutationContext
{
	public static MutationContext instance()
	{
		if( Kit.areAssertionsEnabled() )
			return DebugThreadLocalMutationContext.instance();
		return ProductionThreadLocalMutationContext.instance;
	}

	private ThreadLocalMutationContext()
	{
	}
}
