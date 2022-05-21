package io.github.mikenakis.intertwine.test.comparisons;

import io.github.mikenakis.intertwine.IntertwineFactory;
import io.github.mikenakis.intertwine.implementations.caching.CachingIntertwineFactory;
import io.github.mikenakis.intertwine.test.comparisons.implementations.testing.handwritten.HandwrittenIntertwineFactory;

public final class T03_Handwritten extends Client
{
	private final IntertwineFactory intertwineFactory = new CachingIntertwineFactory( HandwrittenIntertwineFactory.instance );

	public T03_Handwritten()
	{
	}

	@Override protected IntertwineFactory getIntertwineFactory()
	{
		return  intertwineFactory;
	}
}
