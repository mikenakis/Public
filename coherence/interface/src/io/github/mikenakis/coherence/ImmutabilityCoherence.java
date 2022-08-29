package io.github.mikenakis.coherence;

/**
 * Immutability {@link Coherence}.
 *
 * Can never be written; can be read at all times and under any circumstances.
 */
public final class ImmutabilityCoherence implements Coherence.Defaults
{
	public static final Coherence instance = new ImmutabilityCoherence();

	private ImmutabilityCoherence()
	{
	}

	@Override public boolean mustBeReadableAssertion()
	{
		return true;
	}

	@Override public boolean mustBeWritableAssertion()
	{
		return false;
	}

	@Override public String toString()
	{
		return getClass().getSimpleName();
	}
}
