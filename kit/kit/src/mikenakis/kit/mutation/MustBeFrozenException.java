package mikenakis.kit.mutation;

import mikenakis.kit.UncheckedException;

public class MustBeFrozenException extends UncheckedException
{
	public final MutationContext mutationContext;

	public MustBeFrozenException( MutationContext mutationContext ) { this.mutationContext = mutationContext; }
}
