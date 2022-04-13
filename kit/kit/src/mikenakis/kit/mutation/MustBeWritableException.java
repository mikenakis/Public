package mikenakis.kit.mutation;

import mikenakis.kit.UncheckedException;

public class MustBeWritableException extends UncheckedException
{
	public final MutationContext mutationContext;

	public MustBeWritableException( MutationContext mutationContext ) { this.mutationContext = mutationContext; }

	public MustBeWritableException( MutationContext mutationContext, Throwable cause )
	{
		super( cause );
		this.mutationContext = mutationContext;
	}
}
