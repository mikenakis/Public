package io.github.mikenakis.coherence.implementation;

import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.coherence.implementation.exceptions.MustBeReadableException;
import io.github.mikenakis.coherence.implementation.exceptions.MustBeWritableException;

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
