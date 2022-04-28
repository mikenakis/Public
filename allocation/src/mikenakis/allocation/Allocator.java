package mikenakis.allocation;

import mikenakis.kit.Kit;
import mikenakis.kit.coherence.AbstractCoherent;
import mikenakis.kit.coherence.Coherence;
import mikenakis.kit.coherence.implementation.ThreadLocalCoherence;
import mikenakis.kit.logging.Log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link Allocation} factory.
 *
 * @author michael.gr
 */
public final class Allocator extends AbstractCoherent
{
	private static final Allocator instance = new Allocator();

	public static Allocator instance()
	{
		return instance;
	}

	private static final int defaultAllocationSize = 65536;
	private final Map<AllocationKey,Integer> allocationSizes = new ConcurrentHashMap<>();

	private Allocator()
	{
		super( ThreadLocalCoherence.instance() );
	}

	public int getAllocationSize( AllocationKey allocationKey )
	{
		return allocationSizes.computeIfAbsent( allocationKey, k ->
		{
			Log.warning( AllocationKey.class.getSimpleName() + " " + k + " unknown, using default of " + defaultAllocationSize + " bytes." );
			return defaultAllocationSize;
		} );
	}

	public void setAllocationSize( AllocationKey allocationKey, int allocationSize )
	{
		Kit.map.addOrReplace( allocationSizes, allocationKey, allocationSize );
	}

	public Allocation newAllocation( AllocationKey allocationKey )
	{
		int allocationSize = getAllocationSize( allocationKey );
		/* TODO */
		byte[] bytes = new byte[allocationSize];
		Coherence coherence = ThreadLocalCoherence.instance();
		return new Allocation( coherence, this, bytes );
	}

	void release( Allocation allocation )
	{
		/* TODO */
	}
}
