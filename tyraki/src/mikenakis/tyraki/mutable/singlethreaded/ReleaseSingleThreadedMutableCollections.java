package mikenakis.tyraki.mutable.singlethreaded;

import mikenakis.tyraki.mutable.MutableCollections;

/**
 * Release Single-threaded {@link MutableCollections}.
 *
 * @author michael.gr
 */
public final class ReleaseSingleThreadedMutableCollections extends MutableCollections
{
	static final MutableCollections instance = new ReleaseSingleThreadedMutableCollections();

	private ReleaseSingleThreadedMutableCollections()
	{
	}

	@Override public boolean assertCoherence()
	{
		return true;
	}
}
