package mikenakis.kit.mutation;

/**
 * Alternative naming:
 *  Mutable, MutationContext - > Coherent, Coherence
 *  Coherent, Coherence -> Coherable, Coherability
 */
public interface MutationContext
{
	/**
	 * Asserts that it is safe to access mutable state for reading.
	 *
	 * @return always true (otherwise it throws.)
	 */
	boolean mustBeReadableAssertion();

	/**
	 * Asserts that it is safe to modify mutable state.
	 *
	 * @return always true (otherwise it throws.)
	 */
	boolean mustBeWritableAssertion();
}
