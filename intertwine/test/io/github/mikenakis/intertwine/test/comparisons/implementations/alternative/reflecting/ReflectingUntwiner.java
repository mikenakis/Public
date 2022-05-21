package io.github.mikenakis.intertwine.test.comparisons.implementations.alternative.reflecting;

import io.github.mikenakis.intertwine.Anycall;
import io.github.mikenakis.intertwine.Intertwine;
import io.github.mikenakis.intertwine.MethodKey;
import io.github.mikenakis.kit.Kit;

import java.lang.reflect.InvocationTargetException;

class ReflectingUntwiner<T>
{
	private final T exitPoint;

	final Anycall<T> anycall = new Anycall<>()
	{
		@Override public Object anycall( MethodKey<T> key, Object[] arguments )
		{
			ReflectingKey<T> reflectingKey = (ReflectingKey<T>)key;
			try
			{
				return reflectingKey.method.invoke( exitPoint, arguments );
			}
			catch( IllegalAccessException e )
			{
				throw new RuntimeException( e );
			}
			catch( InvocationTargetException e )
			{
				throw Kit.sneakyException( e.getCause() );
			}
		}
	};

	ReflectingUntwiner( Intertwine<T> intertwine, T exitPoint )
	{
		assert intertwine.interfaceType().isAssignableFrom( exitPoint.getClass() );
		this.exitPoint = exitPoint;
	}
}
