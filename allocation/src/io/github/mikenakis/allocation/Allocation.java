package io.github.mikenakis.allocation;

import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.live.AbstractMortalCoherent;
import io.github.mikenakis.live.guard.LifeGuard;

/**
 * Represents a memory allocation.
 *
 * @author michael.gr
 */
public final class Allocation extends AbstractMortalCoherent
{
	private final LifeGuard lifeGuard = LifeGuard.of( this );
	@Override protected LifeGuard lifeGuard() { return lifeGuard; }
	private final Allocator allocator;
	private final byte[] bytes;

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

	public byte[] bytes()
	{
		assert mustBeWritableAssertion();
		return bytes;
	}
}
