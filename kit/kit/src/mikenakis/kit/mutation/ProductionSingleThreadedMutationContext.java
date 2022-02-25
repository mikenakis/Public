package mikenakis.kit.mutation;

/**
 * Release Single-threaded {@link MutationContext}.
 *
 * @author michael.gr
 */
final class ProductionSingleThreadedMutationContext implements MutationContext
{
	static final MutationContext instance = new ProductionSingleThreadedMutationContext();

	private ProductionSingleThreadedMutationContext()
	{
	}

	@Override public boolean isInContextAssertion()
	{
		throw new AssertionError(); //this method should never be invoked in production.
	}

	@Override public boolean isFrozen()
	{
		return false;
	}
}
