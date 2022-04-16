package mikenakis.kit.buffers;

import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.kit.mutation.AbstractCoherent;
import mikenakis.kit.mutation.Coherence;

/**
 * Represents a buffer allocation.
 *
 * @author michael.gr
 */
public final class BufferAllocation extends AbstractCoherent implements Closeable.Defaults
{
	private final LifeGuard lifeGuard = LifeGuard.of( this );
	private final BufferAllocator bufferAllocator;
	public final byte[] bytes;

	BufferAllocation( Coherence coherence, BufferAllocator bufferAllocator, byte[] bytes )
	{
		super( coherence );
		this.bufferAllocator = bufferAllocator;
		this.bytes = bytes;
	}

	@Override public boolean mustBeAliveAssertion()
	{
		assert mustBeReadableAssertion();
		return lifeGuard.mustBeAliveAssertion();
	}

	@Override public void close()
	{
		assert mustBeAliveAssertion();
		assert mustBeWritableAssertion();
		bufferAllocator.release( this );
		lifeGuard.close();
	}
}
