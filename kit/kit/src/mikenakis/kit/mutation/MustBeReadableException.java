package mikenakis.kit.mutation;

import mikenakis.kit.UncheckedException;

public class MustBeReadableException extends UncheckedException
{
	public final MutationContext mutationContext;

	public MustBeReadableException( MutationContext mutationContext ) { this.mutationContext = mutationContext; }

	public MustBeReadableException( MutationContext mutationContext, Throwable cause )
	{
		super( cause );
		this.mutationContext = mutationContext;
	}
}
