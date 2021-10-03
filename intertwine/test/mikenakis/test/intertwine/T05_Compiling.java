package mikenakis.test.intertwine;

import mikenakis.intertwine.IntertwineFactory;
import mikenakis.intertwine.implementations.caching.CachingIntertwineFactory;
import mikenakis.intertwine.implementations.compiling.CompilingIntertwineFactory;

public final class T05_Compiling extends Client
{
	private final IntertwineFactory intertwineFactory = new CachingIntertwineFactory( CompilingIntertwineFactory.instance );

	public T05_Compiling()
	{
	}

	@Override protected IntertwineFactory getIntertwineFactory()
	{
		return intertwineFactory;
	}
}
