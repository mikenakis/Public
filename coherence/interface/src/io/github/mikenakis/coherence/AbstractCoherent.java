package io.github.mikenakis.coherence;

/**
 * Abstract base class for coherence-aware objects.
 */
public abstract class AbstractCoherent implements Coherent
{
	protected final Coherence coherence;

	protected AbstractCoherent( Coherence coherence )
	{
		assert coherence.mustBeWritableAssertion();
		this.coherence = coherence;
	}

	@Override public final Coherence coherence()
	{
		return coherence;
	}

	protected boolean mustBeReadableAssertion()
	{
		return coherence.mustBeReadableAssertion();
	}

	protected boolean mustBeWritableAssertion()
	{
		return coherence.mustBeWritableAssertion();
	}
}
