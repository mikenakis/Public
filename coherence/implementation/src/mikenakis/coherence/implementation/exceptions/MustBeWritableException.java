package mikenakis.coherence.implementation.exceptions;

import mikenakis.coherence.Coherence;
import mikenakis.kit.UncheckedException;

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
