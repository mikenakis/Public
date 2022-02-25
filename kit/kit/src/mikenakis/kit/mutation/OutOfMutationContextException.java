package mikenakis.kit.mutation;

import mikenakis.kit.UncheckedException;

public class OutOfMutationContextException extends UncheckedException
{
	public final MutationContext mutationContext;

	public OutOfMutationContextException( MutationContext mutationContext ) { this.mutationContext = mutationContext; }
}
