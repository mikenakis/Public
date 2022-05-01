package mikenakis.lifetime;

public final class ConcreteMortalWrapper<T> implements MortalWrapper.Defaults<T>, Mortal.Decorator
{
	private final T target;
	private final Mortal delegee;

	public ConcreteMortalWrapper( T target, Mortal delegee )
	{
		this.target = target;
		this.delegee = delegee;
	}

	@Override public T getTarget()
	{
		return target;
	}

	@Override public Mortal getDecoratedMortal()
	{
		return delegee;
	}
}
