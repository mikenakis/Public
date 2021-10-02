package mikenakis.intertwine.implementations.compiling;

import mikenakis.intertwine.AnyCall;
import mikenakis.intertwine.Intertwine;
import mikenakis.kit.Kit;

import java.lang.reflect.InvocationTargetException;

class CompilingUntwiner<T>
{
	private final T exitPoint;

	final AnyCall<T> anycall = new AnyCall<>()
	{
		@Override public Object anyCall( Intertwine.Key<T> key, Object[] arguments )
		{
			CompilingKey<T> compilingKey = (CompilingKey<T>)key;
			try
			{
				return compilingKey.method.invoke( exitPoint, arguments );
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

	CompilingUntwiner( Intertwine<T> intertwine, T exitPoint )
	{
		assert intertwine.interfaceType().isAssignableFrom( exitPoint.getClass() );
		this.exitPoint = exitPoint;
	}
}
