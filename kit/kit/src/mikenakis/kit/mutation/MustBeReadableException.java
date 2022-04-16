package mikenakis.kit.mutation;

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
