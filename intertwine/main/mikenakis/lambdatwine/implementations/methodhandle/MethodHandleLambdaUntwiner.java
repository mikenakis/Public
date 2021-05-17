package mikenakis.lambdatwine.implementations.methodhandle;

import mikenakis.kit.Kit;
import mikenakis.lambdatwine.AnyLambda;

import java.lang.invoke.MethodHandle;

class MethodHandleLambdaUntwiner<T>
{
	private final MethodHandle boundMethodHandle;
	final AnyLambda<T> anyLambda = new AnyLambda<>()
	{
		@Override public Object anyLambda( Object[] arguments )
		{
			return Kit.invokeThrowableThrowingFunction( () -> boundMethodHandle.invokeExact( arguments ) );
		}
	};

	MethodHandleLambdaUntwiner( MethodHandleLambdatwine<T> lambdatwine, T exitPoint )
	{
		assert lambdatwine != null;
		assert exitPoint != null;
		assert lambdatwine.interfaceType().isAssignableFrom( exitPoint.getClass() );
		boundMethodHandle = bind( lambdatwine.methodHandle, exitPoint, lambdatwine.method.getParameterCount() );
	}

	private static MethodHandle bind( MethodHandle methodHandle, Object exitPoint, int parameterCount )
	{
		methodHandle = methodHandle.asType( methodHandle.type().changeReturnType( Object.class ) );
		methodHandle = methodHandle.bindTo( exitPoint );
		methodHandle = methodHandle.asSpreader( Object[].class, parameterCount );
		return methodHandle;
	}
}
