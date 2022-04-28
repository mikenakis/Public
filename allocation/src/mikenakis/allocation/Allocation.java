package mikenakis.allocation;

import mikenakis.kit.coherence.Coherence;
import mikenakis.lifetime.AbstractMortalCoherent;

/**
 * Represents a memory allocation.
 *
 * @author michael.gr
 */
public final class Allocation extends AbstractMortalCoherent
{
	private final Allocator allocator;
	public final byte[] bytes;

	Allocation( Coherence coherence, Allocator allocator, byte[] bytes )
	{
		super( coherence );
		this.allocator = allocator;
		this.bytes = bytes;
	}

	@Override protected void onClose()
	{
		allocator.release( this );
		super.onClose();
	}
}
