package mikenakis.kit.mutation;

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

	@Override public Coherence coherence()
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
