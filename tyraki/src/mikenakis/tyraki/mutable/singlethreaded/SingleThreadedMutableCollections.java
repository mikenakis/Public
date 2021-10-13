package mikenakis.tyraki.mutable.singlethreaded;

import mikenakis.kit.Kit;
import mikenakis.tyraki.mutable.MutableCollections;

/**
 * Single-threaded {@link MutableCollections}.
 *
 * @author michael.gr
 */
public final class SingleThreadedMutableCollections
{
	public static MutableCollections instance()
	{
		if( Kit.areAssertionsEnabled() )
			return DebugSingleThreadedMutableCollections.instance();
		return ReleaseSingleThreadedMutableCollections.instance;
	}

	private SingleThreadedMutableCollections()
	{
	}
}
