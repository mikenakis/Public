package mikenakis.kit.buffers;

import mikenakis.kit.Kit;
import mikenakis.kit.coherence.AbstractCoherent;
import mikenakis.kit.coherence.Coherence;
import mikenakis.kit.coherence.ThreadLocalCoherence;
import mikenakis.kit.logging.Log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Buffer Allocator.
 *
 * @author michael.gr
 */
public final class BufferAllocator extends AbstractCoherent
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
		super( ThreadLocalCoherence.instance() );
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
		Coherence coherence = ThreadLocalCoherence.instance();
		return new BufferAllocation( coherence, this, bytes );
	}

	void release( BufferAllocation bufferAllocation )
	{
		/* TODO */
	}
}
