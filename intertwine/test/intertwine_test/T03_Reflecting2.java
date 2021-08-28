package intertwine_test;

import mikenakis.intertwine.IntertwineFactory;
import mikenakis.intertwine.implementations.caching.CachingIntertwineFactory;
import mikenakis.intertwine.implementations.reflecting2.Reflecting2IntertwineFactory;

public final class T03_Reflecting2 extends Client
{
	private final IntertwineFactory intertwineFactory = new CachingIntertwineFactory( Reflecting2IntertwineFactory.instance );

	public T03_Reflecting2()
	{
	}

	@Override protected IntertwineFactory getIntertwineFactory()
	{
		return intertwineFactory;
	}
}
