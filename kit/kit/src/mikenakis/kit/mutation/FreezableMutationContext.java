package mikenakis.kit.mutation;

public interface FreezableMutationContext extends MutationContext
{
	static ConcreteFreezableMutationContext of()
	{
		MutationContext parentMutationContext = ThreadLocalMutationContext.instance();
		return new ConcreteFreezableMutationContext( parentMutationContext );
	}

	static ConcreteFreezableMutationContext of( MutationContext parentMutationContext )
	{
		return new ConcreteFreezableMutationContext( parentMutationContext );
	}

	/**
	 * Asserts that this {@link FreezableMutationContext} has been frozen.
	 *
	 * @return always true (otherwise it throws.)
	 */
	boolean mustBeFrozenAssertion();

	/**
	 * Asserts that this {@link FreezableMutationContext} has not been frozen.
	 *
	 * @return always true (otherwise it throws.)
	 */
	boolean mustNotBeFrozenAssertion();
}
