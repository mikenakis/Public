package mikenakis.intertwine.implementations.compiling;

import mikenakis.intertwine.Intertwine;
import mikenakis.intertwine.IntertwineFactory;

/**
 * A {@link IntertwineFactory} for {@link CompilingIntertwine}.
 *
 * @author michael.gr
 */
public class CompilingIntertwineFactory implements IntertwineFactory
{
	private final ClassLoader classLoader;

	public CompilingIntertwineFactory( ClassLoader classLoader )
	{
		this.classLoader = classLoader;
	}

	@Override public <T> Intertwine<T> getIntertwine( Class<? super T> interfaceType )
	{
		return new CompilingIntertwine<>( classLoader, interfaceType );
	}
}
