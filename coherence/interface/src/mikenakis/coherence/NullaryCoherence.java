package mikenakis.coherence;

public final class NullaryCoherence implements Coherence.Defaults
{
	public static final Coherence instance = new NullaryCoherence();

	private NullaryCoherence()
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
		return getClass().getSimpleName();
	}
}
