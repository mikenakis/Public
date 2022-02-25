package mikenakis.kit.mutation;

import mikenakis.kit.UncheckedException;

public class FrozenException extends UncheckedException
{
	public final MutationContext mutationContext;

	public FrozenException( MutationContext mutationContext ) { this.mutationContext = mutationContext; }
}
