package io.github.mikenakis.allocation;

import io.github.mikenakis.coherence.AbstractCoherent;
import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.coherence.implementation.ThreadLocalCoherence;
import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.kit.logging.Log;
import io.github.mikenakis.live.Live;

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

	public Live<Allocation> newAllocation( AllocationKey allocationKey )
	{
		int allocationSize = getAllocationSize( allocationKey );
		/* TODO */
		byte[] bytes = new byte[allocationSize];
		Coherence coherence = ThreadLocalCoherence.instance();
		return Allocation.of( coherence, bytes );
	}

	void release( Allocation allocation )
	{
		/* TODO */
	}
}
