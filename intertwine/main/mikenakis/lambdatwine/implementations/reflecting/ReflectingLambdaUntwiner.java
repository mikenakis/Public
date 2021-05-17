package mikenakis.lambdatwine.implementations.reflecting;

import mikenakis.lambdatwine.AnyLambda;

import java.lang.reflect.InvocationTargetException;

class ReflectingLambdaUntwiner<T>
{
	private final ReflectingLambdatwine<T> lambdatwine;
	private final T exitPoint;
	final AnyLambda<T> anyLambda = new AnyLambda<>()
	{
		@Override public Object anyLambda( Object[] arguments )
		{
			try
			{
				return lambdatwine.method.invoke( exitPoint, arguments );
			}
			catch( IllegalAccessException e )
			{
				throw sneakyThrow( e );
			}
			catch( InvocationTargetException e )
			{
				throw sneakyThrow( e.getCause() );
			}
		}
	};

	ReflectingLambdaUntwiner( ReflectingLambdatwine<T> lambdatwine, T exitPoint )
	{
		assert lambdatwine != null;
		assert exitPoint != null;
		assert lambdatwine.interfaceType().isAssignableFrom( exitPoint.getClass() );
		this.lambdatwine = lambdatwine;
		this.exitPoint = exitPoint;
	}

	@SuppressWarnings( "unchecked" )
	private static <T extends Throwable> RuntimeException sneakyThrow( Throwable t ) throws T
	{
		throw (T)t;
	}
}
