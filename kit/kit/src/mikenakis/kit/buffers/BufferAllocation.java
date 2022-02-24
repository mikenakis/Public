package mikenakis.kit.buffers;

import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.kit.mutation.Mutable;

/**
 * Represents a buffer allocation.
 *
 * @author michael.gr
 */
public final class BufferAllocation extends Mutable implements Closeable.Defaults
{
	private final LifeGuard lifeGuard = LifeGuard.of( this );
	private final BufferAllocator bufferAllocator;
	public final byte[] bytes;

	BufferAllocation( BufferAllocator bufferAllocator, byte[] bytes )
	{
		super( bufferAllocator.mutationContext() );
		this.bufferAllocator = bufferAllocator;
		this.bytes = bytes;
	}

	@Override public void close()
	{
		assert isAliveAssertion();
		bufferAllocator.release( this );
		lifeGuard.close();
	}

	@Override public boolean isAliveAssertion()
	{
		assert lifeGuard.isAliveAssertion();
		return true;
	}
}
