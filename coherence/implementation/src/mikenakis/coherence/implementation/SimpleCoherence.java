package mikenakis.coherence.implementation;

import mikenakis.coherence.Coherence;
import mikenakis.coherence.implementation.exceptions.MustBeReadableException;
import mikenakis.coherence.implementation.exceptions.MustBeWritableException;

public abstract class SimpleCoherence implements Coherence.Defaults
{
	protected abstract boolean isEntered();

	@Override public final boolean mustBeReadableAssertion()
	{
		assert isEntered() : new MustBeReadableException( this );
		return true;
	}

	@Override public final boolean mustBeWritableAssertion()
	{
		assert isEntered() : new MustBeWritableException( this );
		return true;
	}

	@Override public String toString()
	{
		return "isEntered: " + isEntered();
	}
}
