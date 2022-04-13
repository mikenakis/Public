package mikenakis.kit.mutation;

public abstract class Mutable
{
	protected final MutationContext mutationContext;

	protected Mutable( MutationContext mutationContext )
	{
		assert mutationContext.mustBeWritableAssertion();
		this.mutationContext = mutationContext;
	}

	public MutationContext mutationContext()
	{
		return mutationContext;
	}

	protected boolean mustBeReadableAssertion()
	{
		return mutationContext.mustBeReadableAssertion();
	}

	protected boolean mustBeWritableAssertion()
	{
		return mutationContext.mustBeWritableAssertion();
	}
}
