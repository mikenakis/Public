package mikenakis.coherence.exceptions;

import mikenakis.coherence.Coherence;

public class MustNotBeFrozenException extends RuntimeException
{
	public final Coherence coherence;

	public MustNotBeFrozenException( Coherence coherence ) { this.coherence = coherence; }
}
