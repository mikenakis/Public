package mikenakis.kit.coherence;

import mikenakis.kit.functional.Function0;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.mutation.Mutable;

/**
 * Base class for objects that are coherence-aware.
 *
 * @author michael.gr
 */
public class Coherent extends Mutable
{
	protected final Coherence coherence;

	public Coherent( Coherence coherence )
	{
		super( coherence.mutationContext() );
		this.coherence = coherence;
		//assert coherenceAssertion();
	}

	/**
	 * Assert coherence.
	 *
	 * @return {@code true} always.
	 */
	public final boolean coherenceAssertion()
	{
		assert coherence.assertCoherence();
		return true;
	}

	public final void cohere( Procedure0 procedure )
	{
		coherence.cohere( procedure );
	}

	public final <R> R cohere( Function0<R> function )
	{
		return coherence.cohere( function );
	}

	public final Coherence getCoherence()
	{
		return coherence;
	}
}
