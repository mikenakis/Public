package io.github.mikenakis.intertwine.test.comparisons;

import io.github.mikenakis.intertwine.IntertwineFactory;
import io.github.mikenakis.intertwine.test.comparisons.implementations.testing.handwritten_compiled.HandWrittenCompiledIntertwineFactory;

public final class T04_HandWrittenCompiled extends Client
{
	private final IntertwineFactory intertwineFactory = HandWrittenCompiledIntertwineFactory.instance;

	public T04_HandWrittenCompiled()
	{
	}

	@Override protected IntertwineFactory getIntertwineFactory()
	{
		return  intertwineFactory;
	}
}
