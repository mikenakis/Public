package mikenakis.kit.mutation;

public abstract class SimpleMutationContext implements MutationContext
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
