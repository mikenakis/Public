package mikenakis.kit.coherence;

public final class UnknownCoherence implements Coherence
{
	public static final Coherence instance = new UnknownCoherence();

	private UnknownCoherence()
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
