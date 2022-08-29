package io.github.mikenakis.coherence;

/**
 * The nullary {@link Coherence}.
 *
 * Is coherent at all times, under any circumstances.
 */
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
