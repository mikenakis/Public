package mikenakis.kit.coherence.implementation;

import mikenakis.kit.UncheckedException;
import mikenakis.kit.coherence.Coherence;

public class MustBeReadableException extends UncheckedException
{
	public final Coherence coherence;

	public MustBeReadableException( Coherence coherence ) { this.coherence = coherence; }

	public MustBeReadableException( Coherence coherence, Throwable cause )
	{
		super( cause );
		this.coherence = coherence;
	}
}
