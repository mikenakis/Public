package io.github.mikenakis.coherence;

/**
 * Abstract base class for immutable objects.
 */
public abstract class ImmutableCoherent implements Coherent
{
	protected ImmutableCoherent()
	{
	}

	@Override public final Coherence coherence()
	{
		return ImmutabilityCoherence.instance;
	}
}
