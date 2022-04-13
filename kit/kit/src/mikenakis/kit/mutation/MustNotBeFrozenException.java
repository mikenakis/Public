package mikenakis.kit.mutation;

import mikenakis.kit.UncheckedException;

public class MustNotBeFrozenException extends UncheckedException
{
	public final MutationContext mutationContext;

	public MustNotBeFrozenException( MutationContext mutationContext ) { this.mutationContext = mutationContext; }
}
