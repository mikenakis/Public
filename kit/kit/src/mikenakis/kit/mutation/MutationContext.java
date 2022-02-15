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
}
