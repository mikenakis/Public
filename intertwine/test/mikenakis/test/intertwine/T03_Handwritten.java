package mikenakis.test.intertwine;

import mikenakis.intertwine.IntertwineFactory;
import mikenakis.intertwine.implementations.caching.CachingIntertwineFactory;
import mikenakis.test.intertwine.handwritten.HandwrittenIntertwineFactory;

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
