package mikenakis.kit.mutation;

public final class UnknownMutationContext implements MutationContext
{
	public static final MutationContext instance = new UnknownMutationContext();

	private UnknownMutationContext()
	{
	}

	@Override public boolean mustBeReadableAssertion()
	{
		return true;
	}

	@Override public boolean mustBeWritableAssertion()
	{
		return true;
	}

	@Override public String toString()
	{
		return "unknown";
	}
}
