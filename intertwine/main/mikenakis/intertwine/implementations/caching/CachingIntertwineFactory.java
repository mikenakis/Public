package mikenakis.intertwine.implementations.caching;

import mikenakis.intertwine.Intertwine;
import mikenakis.intertwine.IntertwineFactory;
import mikenakis.kit.Kit;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Creates {@link Intertwine}s for interfaces.
 *
 * @author Michael Belivanakis (michael.gr)
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
	public final void add( Intertwine<?> intertwine )
	{
		Kit.map.add( cache, intertwine.interfaceType(), intertwine );
	}

	@Override public final <T> Intertwine<T> getIntertwine( Class<? super T> interfaceType )
	{
		assert interfaceType.isInterface();
		synchronized( cache )
		{
			@SuppressWarnings( "unchecked" ) Intertwine<T> existingIntertwine = (Intertwine<T>)Kit.map.tryGet( cache, interfaceType );
			if( existingIntertwine != null )
				return existingIntertwine;
			Intertwine<T> intertwine = delegee.getIntertwine( interfaceType );
			Kit.map.add( cache, interfaceType, intertwine );
			return intertwine;
		}
	}
}
