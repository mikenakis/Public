package mikenakis.kit.coherence.exceptions;

import mikenakis.kit.coherence.Coherence;

public class MustNotBeFrozenException extends RuntimeException
{
	public final Coherence coherence;

	public MustNotBeFrozenException( Coherence coherence ) { this.coherence = coherence; }
}
