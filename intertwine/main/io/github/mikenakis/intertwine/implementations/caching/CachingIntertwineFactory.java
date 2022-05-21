package io.github.mikenakis.intertwine.implementations.caching;

import io.github.mikenakis.intertwine.Intertwine;
import io.github.mikenakis.intertwine.IntertwineFactory;
import io.github.mikenakis.kit.Kit;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Delegates {@link Intertwine} creation to another {@link IntertwineFactory} and caches the results, so that for each interface type, the other
 * {@link IntertwineFactory} will only be asked to create an {@link Intertwine} once.
 * <p>
 * Fringe case: if the creation of an {@link Intertwine} for a certain interface type is requested by more than one thread simultaneously,
 * there is a slight chance that the other {@link IntertwineFactory} will be invoked to create an {@link Intertwine} simultaneously on each thread.
 * In this scenario, the {@link Intertwine} that gets created first is saved, and the rest are discarded.
 * <p>
 *
 * @author michael.gr
 */
public final class CachingIntertwineFactory implements IntertwineFactory
{
	private final IntertwineFactory delegee;
	private final Map<Class<?>,Intertwine<?>> cache = new LinkedHashMap<>();

	public CachingIntertwineFactory( IntertwineFactory delegee )
	{
		this.delegee = delegee;
	}

	/**
	 * Adds an {@link Intertwine} to the cache, essentially making it a predefined {@link Intertwine}.
	 *
	 * @param intertwine the {@link Intertwine} to add.
	 */
	public void add( Intertwine<?> intertwine )
	{
		Kit.map.add( cache, intertwine.interfaceType(), intertwine );
	}

	@Override public <T> Intertwine<T> getIntertwine( Class<? super T> interfaceType )
	{
		assert interfaceType.isInterface();

		/**
		 * Note: we could lock the cache just once, and use {@link Map#computeIfAbsent(Object, java.util.function.Function)};
		 * however, that would keep the cache locked for as long as it takes for the delegee to create the {@link Intertwine}.
		 * In a multi-threaded scenario, different threads are likely to be asking for {@link Intertwine}s of different interface types,
		 * so there is no point in making them all wait for each other.
		 * For this reason, we use the following two-step approach.
		 */

		/**
		 * first, lock the cache only for as long as it takes to check whether the requested {@link Intertwine} has already been cached; if so, we are done.
		 */
		Intertwine<T> existingIntertwine = Kit.sync.synchronize( cache, () ->
		{
			@SuppressWarnings( "unchecked" ) Intertwine<T> result = (Intertwine<T>)Kit.map.tryGet( cache, interfaceType );
			return result;
		} );
		if( existingIntertwine != null )
			return existingIntertwine;

		/**
		 * Then, and while the cache is _not_ locked, invoke the other {@link IntertwineFactory} to create the requested {@link Intertwine}.
		 */
		Intertwine<T> intertwine = delegee.getIntertwine( interfaceType );

		/**
		 * Once the requested {@link Intertwine} has been created, lock the cache again, and first check whether another instance of the requested
		 * {@link Intertwine} has already been cached by now; if so, then discard the newly created instance and return the cached one.
		 * Otherwise, cache the new instance and return it.
		 */
		return Kit.sync.synchronize( cache, () ->
		{
			@SuppressWarnings( "unchecked" ) Intertwine<T> existing = (Intertwine<T>)Kit.map.tryGet( cache, interfaceType );
			if( existing != null )
				return existing;
			Kit.map.add( cache, interfaceType, intertwine );
			return intertwine;
		} );
	}
}
