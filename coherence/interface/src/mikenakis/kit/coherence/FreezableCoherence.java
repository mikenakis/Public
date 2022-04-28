package mikenakis.kit.coherence;

public interface FreezableCoherence extends Coherence
{
	/**
	 * Asserts that this {@link FreezableCoherence} has been frozen.
	 *
	 * @return always true (otherwise it throws.)
	 */
	boolean mustBeFrozenAssertion();

	/**
	 * Asserts that this {@link FreezableCoherence} has not been frozen.
	 *
	 * @return always true (otherwise it throws.)
	 */
	boolean mustNotBeFrozenAssertion();
}
