package mikenakis.kit.coherence;

/**
 * An assertion-only abstraction of a state of coherence, indicating whether it is permissible to read or write.
 */
public interface Coherence
{
	/**
	 * Asserts that it is safe to read mutable state.
	 *
	 * @return always true (otherwise it throws.)
	 */
	boolean mustBeReadableAssertion();

	/**
	 * Asserts that it is safe to write mutable state.
	 *
	 * @return always true (otherwise it throws.)
	 */
	boolean mustBeWritableAssertion();
}
