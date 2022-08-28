package io.github.mikenakis.coherence;

/**
 * An assertion-only abstraction of a state of coherence, indicating whether it is permissible to read or write.
 * Not to be confused with Cohesion, see <a href="https://en.wikipedia.org/wiki/Cohesion_(computer_science)">Wikipedia: Cohesion</a>
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

	interface Defaults extends Coherence
	{
	}
}
