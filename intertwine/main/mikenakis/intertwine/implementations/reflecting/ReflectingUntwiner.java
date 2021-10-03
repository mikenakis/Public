package mikenakis.intertwine.implementations.reflecting;

import mikenakis.intertwine.AnyCall;
import mikenakis.intertwine.Intertwine;
import mikenakis.intertwine.MethodKey;
import mikenakis.kit.Kit;

import java.lang.reflect.InvocationTargetException;

class ReflectingUntwiner<T>
{
	private final T exitPoint;

	final AnyCall<T> anycall = new AnyCall<>()
	{
		@Override public Object anyCall( MethodKey<T> key, Object[] arguments )
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
