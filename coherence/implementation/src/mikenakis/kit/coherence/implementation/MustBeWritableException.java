package mikenakis.kit.coherence.implementation;

import mikenakis.kit.UncheckedException;
import mikenakis.kit.coherence.Coherence;

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
