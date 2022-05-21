package io.github.mikenakis.intertwine;

import io.github.mikenakis.intertwine.implementations.caching.CachingIntertwineFactory;
import io.github.mikenakis.intertwine.implementations.compiling.CompilingIntertwineFactory;

/**
 * Obtains {@link Intertwine}s for interfaces.
 *
 * @author michael.gr
 */
public interface IntertwineFactory
{
	/**
	 * The global instance of intertwine.
	 */
	IntertwineFactory instance = new CachingIntertwineFactory( new CompilingIntertwineFactory() );

	/**
	 * Gets an {@link Intertwine} for a given interface.
	 *
	 * @param interfaceType the {@link Class} of the interface.
	 * @param <T>           the type of the class of the interface.
	 *
	 * @return an {@link Intertwine} for the given interface.
	 */
	<T> Intertwine<T> getIntertwine( Class<? super T> interfaceType );
}
