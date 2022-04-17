package mikenakis.kit.coherence;

import mikenakis.kit.UncheckedException;

public class MustNotBeFrozenException extends UncheckedException
{
	public final Coherence coherence;

	public MustNotBeFrozenException( Coherence coherence ) { this.coherence = coherence; }
}
