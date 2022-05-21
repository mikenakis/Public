package io.github.mikenakis.intertwine.test.comparisons;

import io.github.mikenakis.intertwine.IntertwineFactory;
import io.github.mikenakis.intertwine.implementations.caching.CachingIntertwineFactory;
import io.github.mikenakis.intertwine.implementations.compiling.CompilingIntertwineFactory;

public final class T05_Compiling extends Client
{
	private final IntertwineFactory intertwineFactory = new CachingIntertwineFactory( new CompilingIntertwineFactory() );

	public T05_Compiling()
	{
	}

	@Override protected IntertwineFactory getIntertwineFactory()
	{
		return intertwineFactory;
	}
}
