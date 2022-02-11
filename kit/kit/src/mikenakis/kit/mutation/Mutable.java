package mikenakis.kit.mutation;

public abstract class Mutable
{
	public final MutationContext mutationContext;

	protected Mutable( MutationContext mutationContext )
	{
		//assert mutationContext.inContextAssertion(); TODO: enable this assertion!
		this.mutationContext = mutationContext;
	}

	public MutationContext getMutationContext()
	{
		return mutationContext;
	}

	public boolean inMutationContextAssertion()
	{
		assert mutationContext.inContextAssertion();
		return true;
	}
}
