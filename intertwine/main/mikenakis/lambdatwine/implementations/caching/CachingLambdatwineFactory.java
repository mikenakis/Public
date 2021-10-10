package mikenakis.lambdatwine.implementations.caching;

import mikenakis.intertwine.Intertwine;
import mikenakis.kit.Kit;
import mikenakis.lambdatwine.Lambdatwine;
import mikenakis.lambdatwine.LambdatwineFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Creates {@link Intertwine}s for interfaces.
 *
 * @author michael.gr
 */
public final class CachingLambdatwineFactory implements LambdatwineFactory
{
	private final LambdatwineFactory delegee;
	private final Map<Class<?>,Lambdatwine<?>> cache = new LinkedHashMap<>();

	public CachingLambdatwineFactory( LambdatwineFactory delegee )
	{
		this.delegee = delegee;
	}

	/**
	 * Adds a {@link Lambdatwine} to the cache, essentially making it a predefined {@link Lambdatwine}.
	 *
	 * @param lambdatwine the {@link Lambdatwine} to add.
	 */
	public final void add( Lambdatwine<?> lambdatwine )
	{
		Kit.map.add( cache, lambdatwine.interfaceType(), lambdatwine );
	}

	@Override public final <T> Lambdatwine<T> getLambdatwine( Class<T> interfaceType )
	{
		assert interfaceType.isInterface();
		synchronized( cache )
		{
			@SuppressWarnings( "unchecked" ) Lambdatwine<T> existingLambdatwine = (Lambdatwine<T>)Kit.map.tryGet( cache, interfaceType );
			if( existingLambdatwine != null )
				return existingLambdatwine;
			Lambdatwine<T> lambdatwine = delegee.getLambdatwine( interfaceType );
			Kit.map.add( cache, interfaceType, lambdatwine );
			return lambdatwine;
		}
	}
}
