package mikenakis.kit.mutation;

/**
 * Release Single-threaded {@link MutationContext}.
 *
 * @author michael.gr
 */
final class ReleaseSingleThreadedMutationContext extends MutationContext
{
	static final MutationContext instance = new ReleaseSingleThreadedMutationContext();

	private ReleaseSingleThreadedMutationContext()
	{
	}

	@Override public boolean inContextAssertion()
	{
		return true;
	}
}
