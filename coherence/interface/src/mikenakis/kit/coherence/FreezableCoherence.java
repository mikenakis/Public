package mikenakis.kit.coherence;

import mikenakis.kit.coherence.exceptions.MustBeFrozenException;
import mikenakis.kit.coherence.exceptions.MustNotBeFrozenException;

public interface FreezableCoherence extends Coherence
{
	/**
	 * Checks whether this {@link FreezableCoherence} is frozen.
	 */
	boolean isFrozen();

	/**
	 * Asserts that this {@link FreezableCoherence} has been frozen.
	 *
	 * @return always true (otherwise it throws.)
	 */
	/* final */ default boolean mustBeFrozenAssertion()
	{
		assert isFrozen() : new MustBeFrozenException( this );
		return true;
	}

	/**
	 * Asserts that this {@link FreezableCoherence} has not been frozen.
	 *
	 * @return always true (otherwise it throws.)
	 */
	/* final */ default boolean mustNotBeFrozenAssertion()
	{
		assert !isFrozen() : new MustNotBeFrozenException( this );
		return true;
	}
}
