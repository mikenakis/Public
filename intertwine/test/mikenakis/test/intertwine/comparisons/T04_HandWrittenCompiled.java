package mikenakis.test.intertwine.comparisons;

import mikenakis.intertwine.IntertwineFactory;
import mikenakis.test.intertwine.comparisons.implementations.testing.handwritten_compiled.HandWrittenCompiledIntertwineFactory;

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
