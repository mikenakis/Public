package mikenakis.intertwine.implementations.reflecting2;

import mikenakis.intertwine.AnyCall;
import mikenakis.intertwine.Intertwine;
import mikenakis.kit.Kit;

import java.lang.reflect.InvocationTargetException;

class Reflecting2Untwiner<T>
{
	private final T exitPoint;

	final AnyCall<T> anycall = new AnyCall<>()
	{
		@Override public Object anyCall( Intertwine.Key<T> key, Object[] arguments )
		{
			Reflecting2Key<T> reflectingKey = (Reflecting2Key<T>)key;
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

	Reflecting2Untwiner( Intertwine<T> intertwine, T exitPoint )
	{
		assert intertwine.interfaceType().isAssignableFrom( exitPoint.getClass() );
		this.exitPoint = exitPoint;
	}
}
