package mikenakis.test.intertwine.comparisons;

import mikenakis.intertwine.IntertwineFactory;
import mikenakis.intertwine.implementations.caching.CachingIntertwineFactory;
import mikenakis.test.intertwine.comparisons.implementations.alternative.methodhandle.MethodHandleIntertwineFactory;

public final class T02_MethodHandle extends Client
{
	private final IntertwineFactory intertwineFactory = new CachingIntertwineFactory( MethodHandleIntertwineFactory.instance );

	public T02_MethodHandle()
	{
	}

	@Override protected IntertwineFactory getIntertwineFactory()
	{
		return intertwineFactory;
	}
}
