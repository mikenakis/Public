package mikenakis.kit.mutation;

public interface MutationContext
{
	/**
	 * Asserts that we are currently inside this {@link MutationContext} and it is safe to access mutable state.
	 *
	 * @return always true
	 */
	boolean inContextAssertion();
}
