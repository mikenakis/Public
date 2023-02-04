package io.github.mikenakis.intertwine.implementations.compiling;

import io.github.mikenakis.intertwine.Intertwine;
import io.github.mikenakis.intertwine.IntertwineFactory;

/**
 * A {@link IntertwineFactory} for {@link CompilingIntertwine}.
 *
 * @author michael.gr
 */
public class CompilingIntertwineFactory implements IntertwineFactory
{
	public CompilingIntertwineFactory()
	{
	}

	@Override public <T> Intertwine<T> getIntertwine( Class<T> interfaceType, boolean implementDefaultMethods )
	{
		return new CompilingIntertwine<>( interfaceType, implementDefaultMethods );
	}
}
