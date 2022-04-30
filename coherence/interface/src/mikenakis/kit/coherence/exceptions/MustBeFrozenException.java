package mikenakis.kit.coherence.exceptions;

import mikenakis.kit.coherence.Coherence;

public class MustBeFrozenException extends RuntimeException
{
	public final Coherence coherence;

	public MustBeFrozenException( Coherence coherence ) { this.coherence = coherence; }
}
