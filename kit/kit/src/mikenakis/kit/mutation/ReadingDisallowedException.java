package mikenakis.kit.mutation;

import mikenakis.kit.UncheckedException;

public class ReadingDisallowedException extends UncheckedException
{
	public final MutationContext mutationContext;

	public ReadingDisallowedException( MutationContext mutationContext ) { this.mutationContext = mutationContext; }
}
