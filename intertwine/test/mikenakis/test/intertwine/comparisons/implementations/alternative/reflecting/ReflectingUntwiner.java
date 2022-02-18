package mikenakis.test.intertwine.comparisons.implementations.alternative.reflecting;

import mikenakis.intertwine.Anycall;
import mikenakis.intertwine.Intertwine;
import mikenakis.intertwine.MethodKey;
import mikenakis.kit.Kit;

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
