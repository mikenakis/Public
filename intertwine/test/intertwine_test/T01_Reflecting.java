package intertwine_test;

import mikenakis.intertwine.IntertwineFactory;
import mikenakis.intertwine.implementations.caching.CachingIntertwineFactory;
import mikenakis.intertwine.implementations.reflecting.ReflectingIntertwineFactory;

public final class T01_Reflecting extends Client
{
	private final IntertwineFactory intertwineFactory = new CachingIntertwineFactory( ReflectingIntertwineFactory.instance );

	public T01_Reflecting()
	{
	}

	@Override protected IntertwineFactory getIntertwineFactory()
	{
		return intertwineFactory;
	}
}
