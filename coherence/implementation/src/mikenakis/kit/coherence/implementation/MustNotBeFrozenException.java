package mikenakis.kit.coherence.implementation;

import mikenakis.kit.UncheckedException;
import mikenakis.kit.coherence.Coherence;

public class MustNotBeFrozenException extends UncheckedException
{
	public final Coherence coherence;

	public MustNotBeFrozenException( Coherence coherence ) { this.coherence = coherence; }
}
