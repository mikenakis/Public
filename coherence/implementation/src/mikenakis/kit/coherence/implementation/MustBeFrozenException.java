package mikenakis.kit.coherence.implementation;

import mikenakis.kit.UncheckedException;
import mikenakis.kit.coherence.Coherence;

public class MustBeFrozenException extends UncheckedException
{
	public final Coherence coherence;

	public MustBeFrozenException( Coherence coherence ) { this.coherence = coherence; }
}
