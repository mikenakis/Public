package mikenakis.kit.mutation;

import mikenakis.kit.Kit;

/**
 * Single-threaded {@link MutationContext}.
 *
 * @author michael.gr
 */
public final class SingleThreadedMutationContext
{
	public static MutationContext instance()
	{
		if( Kit.areAssertionsEnabled() )
			return DebugSingleThreadedMutationContext.instance();
		return ProductionSingleThreadedMutationContext.instance;
	}

	private SingleThreadedMutationContext()
	{
	}
}
