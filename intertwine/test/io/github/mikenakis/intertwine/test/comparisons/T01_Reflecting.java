package io.github.mikenakis.intertwine.test.comparisons;

import io.github.mikenakis.intertwine.IntertwineFactory;
import io.github.mikenakis.intertwine.implementations.caching.CachingIntertwineFactory;
import io.github.mikenakis.intertwine.test.comparisons.implementations.alternative.reflecting.ReflectingIntertwineFactory;

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
