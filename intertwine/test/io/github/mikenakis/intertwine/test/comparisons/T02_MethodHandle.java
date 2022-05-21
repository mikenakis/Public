package io.github.mikenakis.intertwine.test.comparisons;

import io.github.mikenakis.intertwine.IntertwineFactory;
import io.github.mikenakis.intertwine.implementations.caching.CachingIntertwineFactory;
import io.github.mikenakis.intertwine.test.comparisons.implementations.alternative.methodhandle.MethodHandleIntertwineFactory;

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
