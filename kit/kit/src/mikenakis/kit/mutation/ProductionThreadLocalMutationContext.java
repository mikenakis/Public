package mikenakis.kit.mutation;

/**
 * Release Single-threaded {@link MutationContext}.
 *
 * @author michael.gr
 */
final class ProductionThreadLocalMutationContext implements MutationContext
{
	static final MutationContext instance = new ProductionThreadLocalMutationContext();

	private ProductionThreadLocalMutationContext()
	{
	}

	@Override public boolean inContextAssertion()
	{
		throw new AssertionError(); //this method should never be invoked in production.
	}

	@Override public boolean isFrozen()
	{
		return false;
	}
}
