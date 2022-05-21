package io.github.mikenakis.coherence.exceptions;

import io.github.mikenakis.coherence.Coherence;

public class MustNotBeFrozenException extends RuntimeException
{
	public final Coherence coherence;

	public MustNotBeFrozenException( Coherence coherence ) { this.coherence = coherence; }
}
