package mikenakis.kit.coherence;

public abstract class SimpleCoherence implements Coherence
{
	protected abstract boolean isEntered();

	@Override public final boolean mustBeReadableAssertion()
	{
		assert isEntered() : new MustBeReadableException( this );
		return true;
	}

	@Override public final boolean mustBeWritableAssertion()
	{
		assert isEntered() : new MustBeWritableException( this );
		return true;
	}

	@Override public String toString()
	{
		return "isEntered: " + isEntered();
	}
}
