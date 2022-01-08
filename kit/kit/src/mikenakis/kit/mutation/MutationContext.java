package mikenakis.kit.mutation;

import mikenakis.kit.Kit;
import mikenakis.kit.functional.Function1;
import mikenakis.kit.functional.Procedure1;

public interface MutationContext
{
	/**
	 * Asserts that we are currently inside this {@link MutationContext} and it is safe to access mutable state.
	 *
	 * @return always true
	 */
	boolean inContextAssertion();

	static void tryWithLocal( Procedure1<MutationContext> procedure ) //TODO in many cases where this is used, it is unnecessary and it can be refactored out.
	{
		Kit.tryWith( new LocalMutationContext(), procedure );
	}

	static <T> T tryGetWithLocal( Function1<T,MutationContext> function ) //TODO in many cases where this is used, it is unnecessary and it can be refactored out.
	{
		return Kit.tryGetWith( new LocalMutationContext(), function );
	}
}
