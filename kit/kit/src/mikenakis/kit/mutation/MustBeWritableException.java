package mikenakis.kit.mutation;

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
