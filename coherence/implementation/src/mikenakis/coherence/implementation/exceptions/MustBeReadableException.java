package mikenakis.coherence.implementation.exceptions;

import mikenakis.coherence.Coherence;
import mikenakis.kit.UncheckedException;

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
