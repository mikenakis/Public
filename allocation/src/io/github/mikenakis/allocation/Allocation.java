package io.github.mikenakis.allocation;

import io.github.mikenakis.coherence.AbstractCoherent;
import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.coherence.Coherent;
import io.github.mikenakis.live.Live;

/**
 * Represents a memory allocation.
 *
 * @author michael.gr
 */
public interface Allocation extends Coherent
{
	byte[] bytes();

	static Live<Allocation> of( Coherence coherence, byte[] bytes )
	{
		final class Implementation extends AbstractCoherent implements Allocation
		{
			private Implementation()
			{
				super( coherence );
			}

			private void close()
			{
				Allocator.instance().release( this );
			}

			@Override public byte[] bytes()
			{
				assert mustBeWritableAssertion();
				return bytes;
			}
		}

		var result = new Implementation();
		return Live.of( result, result::close );
	}
}
