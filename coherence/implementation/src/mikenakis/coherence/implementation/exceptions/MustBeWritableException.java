package mikenakis.coherence.implementation.exceptions;

import mikenakis.kit.UncheckedException;
import mikenakis.coherence.Coherence;

public class MustBeWritableException extends UncheckedException
{
	public final Coherence coherence;

	public MustBeWritableException( Coherence coherence ) { this.coherence = coherence; }

	public MustBeWritableException( Coherence coherence, Throwable cause )
	{
		super( cause );
		this.coherence = coherence;
	}
}
