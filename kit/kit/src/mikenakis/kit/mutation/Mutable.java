package mikenakis.kit.mutation;

public abstract class Mutable
{
	protected final MutationContext mutationContext;

	protected Mutable( MutationContext mutationContext )
	{
		assert mutationContext.inContextAssertion();
		this.mutationContext = mutationContext;
	}

	public MutationContext mutationContext()
	{
		return mutationContext;
	}

	public boolean isFrozen()
	{
		return mutationContext.isFrozen();
	}

	public boolean canReadAssertion()
	{
		assert mutationContext.canReadAssertion();
		return true;
	}

	public boolean canMutateAssertion()
	{
		assert mutationContext.canMutateAssertion();
		return true;
	}
}
