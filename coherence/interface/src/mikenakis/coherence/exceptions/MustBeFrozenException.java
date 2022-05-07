package mikenakis.coherence.exceptions;

import mikenakis.coherence.Coherence;

public class MustBeFrozenException extends RuntimeException
{
	public final Coherence coherence;

	public MustBeFrozenException( Coherence coherence ) { this.coherence = coherence; }
}
