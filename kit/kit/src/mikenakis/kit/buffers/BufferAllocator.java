package mikenakis.kit.buffers;

import mikenakis.kit.Kit;
import mikenakis.kit.logging.Log;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;
import mikenakis.kit.mutation.ThreadLocalMutationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Buffer Allocator.
 *
 * @author michael.gr
 */
public final class BufferAllocator extends Mutable
{
	private static final BufferAllocator instance = new BufferAllocator();

	public static BufferAllocator instance()
	{
		return instance;
	}

	private static final int defaultBufferSize = 65536;
	private final Map<BufferKey,Integer> bufferSizes = new ConcurrentHashMap<>();

	private BufferAllocator()
	{
		super( ThreadLocalMutationContext.instance() );
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
		MutationContext mutationContext = ThreadLocalMutationContext.instance();
		return new BufferAllocation( mutationContext, this, bytes );
	}

	void release( BufferAllocation bufferAllocation )
	{
		/* TODO */
	}
}
