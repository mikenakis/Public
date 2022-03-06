package mikenakis.kit.coherence;

import mikenakis.kit.functional.Function0;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.ThreadLocalMutationContext;

/**
 * Production {@link Coherence}.
 *
 * @author michael.gr
 */
final class ReleaseSingleThreadedCoherence extends Mutable implements Coherence.Defaults
{
	static final Coherence instance = new ReleaseSingleThreadedCoherence();

	private ReleaseSingleThreadedCoherence()
	{
		super( ThreadLocalMutationContext.instance() );
	}

	@Override public <R> R cohere( Function0<R> function )
	{
		return function.invoke();
	}

	@Override public void cohere( Procedure0 procedure )
	{
		procedure.invoke();
	}

	@Override public boolean assertCoherence()
	{
		throw new RuntimeException(); //we do not expect this to be invoked on production.
	}
}
