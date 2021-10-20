package mikenakis.kit.mutation;

public abstract class MutationContext
{
	/**
	 * Asserts that we are currently inside this {@link MutationContext} and it is safe to access mutable state.
	 *
	 * @return always true
	 */
	public abstract boolean inContextAssertion();
}
