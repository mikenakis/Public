package mikenakis.test.intertwine;

import mikenakis.intertwine.IntertwineFactory;
import mikenakis.intertwine.implementations.caching.CachingIntertwineFactory;
import mikenakis.intertwine.implementations.compiling.CompilingIntertwineFactory;

public final class T04_Compiling extends Client
{
	private final IntertwineFactory intertwineFactory = new CachingIntertwineFactory( CompilingIntertwineFactory.instance );

	public T04_Compiling()
	{
	}

	@Override protected IntertwineFactory getIntertwineFactory()
	{
		return intertwineFactory;
	}
}
