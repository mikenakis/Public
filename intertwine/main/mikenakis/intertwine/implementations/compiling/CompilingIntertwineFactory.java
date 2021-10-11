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
	public static CompilingIntertwineFactory instance = new CompilingIntertwineFactory();

	private CompilingIntertwineFactory()
	{
	}

	@Override public <T> Intertwine<T> getIntertwine( Class<? super T> interfaceType )
	{
		return new CompilingIntertwine<>( this, interfaceType );
	}
}
