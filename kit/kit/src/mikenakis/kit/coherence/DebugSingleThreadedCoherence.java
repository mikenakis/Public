package mikenakis.kit.coherence;

import mikenakis.kit.functional.Function0;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.SingleThreadedMutationContext;

/**
 * Single-threaded {@link Coherence}.
 *
 * @author michael.gr
 */
final class DebugSingleThreadedCoherence extends Mutable implements Coherence.Defaults
{
	private static final ThreadLocal<DebugSingleThreadedCoherence> instance = ThreadLocal.withInitial( DebugSingleThreadedCoherence::new );

	public static DebugSingleThreadedCoherence instance()
	{
		return instance.get();
	}

	private DebugSingleThreadedCoherence()
	{
		super( SingleThreadedMutationContext.instance() );
	}

	@Override public <R> R cohere( Function0<R> function )
	{
		assert assertCoherence();
		return function.invoke();
	}

	@Override public void cohere( Procedure0 procedure )
	{
		assert assertCoherence();
		procedure.invoke();
	}

	@Override public boolean assertCoherence()
	{
		assert inMutationContextAssertion();
		return true;
	}

	@Override public String toString()
	{
		return mutationContext.toString();
	}
}
