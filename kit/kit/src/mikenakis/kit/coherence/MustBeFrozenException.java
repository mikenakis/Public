package mikenakis.kit.coherence;

import mikenakis.kit.UncheckedException;

public class MustBeFrozenException extends UncheckedException
{
	public final Coherence coherence;

	public MustBeFrozenException( Coherence coherence ) { this.coherence = coherence; }
}
