package io.github.mikenakis.coherence.exceptions;

import io.github.mikenakis.coherence.Coherence;

public class MustBeFrozenException extends RuntimeException
{
	public final Coherence coherence;

	public MustBeFrozenException( Coherence coherence ) { this.coherence = coherence; }
}
