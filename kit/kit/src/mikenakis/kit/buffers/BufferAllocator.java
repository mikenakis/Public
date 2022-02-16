package mikenakis.kit.buffers;

import mikenakis.kit.Kit;
import mikenakis.kit.logging.Log;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Buffer Allocator.
 *
 * @author michael.gr
 */
public final class BufferAllocator extends Mutable
{
	public static BufferAllocator of( MutationContext mutationContext, int defaultBufferSize )
	{
		return new BufferAllocator( mutationContext, defaultBufferSize );
	}

	private final int defaultBufferSize;
	private final Map<BufferKey,Integer> bufferSizes = new ConcurrentHashMap<>();

	private BufferAllocator( MutationContext mutationContext, int defaultBufferSize )
	{
		super( mutationContext );
		this.defaultBufferSize = defaultBufferSize;
	}

	public int getBufferSize( BufferKey bufferKey )
	{
		return bufferSizes.computeIfAbsent( bufferKey, k ->
		{
			Log.warning( BufferKey.class.getSimpleName() + " " + k + " unknown, using default of " + defaultBufferSize + " bytes." );
			return defaultBufferSize;
		} );
	}

	public void setBufferSize( BufferKey bufferKey, int bufferSize )
	{
		Kit.map.addOrReplace( bufferSizes, bufferKey, bufferSize );
	}

	public BufferAllocation newBufferAllocation( BufferKey bufferKey )
	{
		int bufferSize = getBufferSize( bufferKey );
		/* TODO */
		byte[] bytes = new byte[bufferSize];
		return new BufferAllocation( this, bytes );
	}

	void release( BufferAllocation bufferAllocation )
	{
		/* TODO */
	}
}
