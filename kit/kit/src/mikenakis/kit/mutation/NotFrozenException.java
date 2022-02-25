package mikenakis.kit.mutation;

import mikenakis.kit.UncheckedException;

public class NotFrozenException extends UncheckedException
{
	public final MutationContext mutationContext;

	public NotFrozenException( MutationContext mutationContext ) { this.mutationContext = mutationContext; }
}
