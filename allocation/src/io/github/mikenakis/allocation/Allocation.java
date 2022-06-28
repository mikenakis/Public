package io.github.mikenakis.allocation;

import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.coherence.Coherent;
import io.github.mikenakis.live.AbstractMortalCoherent;
import io.github.mikenakis.live.Live;
import io.github.mikenakis.live.guard.LifeGuard;

/**
 * Represents a memory allocation.
 *
 * @author michael.gr
 */
public interface Allocation extends Coherent
{
	static Live<Allocation> of( Coherence coherence, byte[] bytes )
	{
		final class Implementation extends AbstractMortalCoherent implements Allocation
		{
			private final LifeGuard lifeGuard = LifeGuard.of( this );
			@Override protected LifeGuard lifeGuard() { return lifeGuard; }
			private final byte[] bytes;

			private Implementation( Coherence coherence, byte[] bytes )
			{
				super( coherence );
				this.bytes = bytes;
			}

			@Override protected void onClose()
			{
				Allocator.instance().release( this );
				super.onClose();
			}

			@Override public byte[] bytes()
			{
				assert mustBeWritableAssertion();
				return bytes;
			}
		}

		var result = new Implementation( coherence, bytes );
		return Live.of( result, result::close );
	}

	byte[] bytes();
}
