package mikenakis.kit.mutation;

public interface MutationContext
{
	/**
	 * Checks whether it is currently safe to access this {@link MutationContext}.
	 */
	boolean isInContextAssertion();

	/**
	 * Checks whether this {@link MutationContext} has been frozen.
	 */
	boolean isFrozen();

	/**
	 * Asserts that this {@link MutationContext} has been frozen.
	 *
	 * @return always true (otherwise it throws.)
	 */
	default boolean isFrozenAssertion()
	{
		assert isFrozen() : new NotFrozenException( this );
		return true;
	}

	/**
	 * Asserts that this {@link MutationContext} has not been frozen.
	 *
	 * @return always true (otherwise it throws.)
	 */
	default boolean isNotFrozenAssertion()
	{
		assert !isFrozen() : new FrozenException( this );
		return true;
	}

	/**
	 * Asserts that it is safe to access mutable state for reading.
	 *
	 * @return always true (otherwise it throws.)
	 */
	default boolean canReadAssertion()
	{
		assert isFrozen() || isInContextAssertion() : new ReadingDisallowedException( this );
		return true;
	}

	/**
	 * Asserts that it is safe to modify mutable state.
	 *
	 * @return always true (otherwise it throws.)
	 */
	default boolean canMutateAssertion()
	{
		assert !isFrozen() && isInContextAssertion() : new MutationDisallowedException( this );
		return true;
	}
}
