package io.github.mikenakis.coherence.implementation.exceptions;

import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.kit.UncheckedException;

public class MustBeWritableException extends UncheckedException
{
	public final Coherence coherence;

	public MustBeWritableException( Coherence coherence ) { this.coherence = coherence; }

	public MustBeWritableException( Coherence coherence, Throwable cause )
	{
		super( cause );
		this.coherence = coherence;
	}
}
