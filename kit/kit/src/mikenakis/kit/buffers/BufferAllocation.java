package mikenakis.kit.buffers;

import mikenakis.kit.lifetime.AbstractMortalCoherent;
import mikenakis.kit.mutation.Coherence;

/**
 * Represents a buffer allocation.
 *
 * @author michael.gr
 */
public final class BufferAllocation extends AbstractMortalCoherent
{
	private final BufferAllocator bufferAllocator;
	public final byte[] bytes;

	BufferAllocation( Coherence coherence, BufferAllocator bufferAllocator, byte[] bytes )
	{
		super( coherence );
		this.bufferAllocator = bufferAllocator;
		this.bytes = bytes;
	}

	@Override protected void onClose()
	{
		bufferAllocator.release( this );
		super.onClose();
	}
}
