package mikenakis.test.intertwine;

import mikenakis.intertwine.IntertwineFactory;
import mikenakis.test.intertwine.implementations.testing.handwritten_compiled.HandWrittenCompiledIntertwineFactory;

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
