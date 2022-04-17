package mikenakis.kit.coherence;

public interface FreezableCoherence extends Coherence
{
	static ConcreteFreezableCoherence of()
	{
		return of( ThreadLocalCoherence.instance() );
	}

	static ConcreteFreezableCoherence of( Coherence parentCoherence )
	{
		return new ConcreteFreezableCoherence( parentCoherence );
	}

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
