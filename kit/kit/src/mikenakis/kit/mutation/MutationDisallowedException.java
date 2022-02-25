package mikenakis.kit.mutation;

import mikenakis.kit.UncheckedException;

public class MutationDisallowedException extends UncheckedException
{
	public final MutationContext mutationContext;

	public MutationDisallowedException( MutationContext mutationContext ) { this.mutationContext = mutationContext; }
}
