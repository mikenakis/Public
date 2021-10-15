package mikenakis.test.intertwine.implementations.alternative.methodhandle;

import mikenakis.intertwine.AnyCall;
import mikenakis.intertwine.MethodKey;
import mikenakis.kit.Kit;
import mikenakis.kit.logging.Log;

import java.lang.invoke.MethodHandle;
import java.util.Map;
import java.util.stream.Collectors;

class MethodHandleUntwiner<T>
{
	private final T exitPoint;
	private final Map<MethodHandleKey<T>,MethodHandle> boundMethodHandles;

	MethodHandleUntwiner( MethodHandleIntertwine<T> intertwine, T exitPoint )
	{
		assert intertwine.interfaceType().isAssignableFrom( exitPoint.getClass() );
		this.exitPoint = exitPoint;
		boundMethodHandles = intertwine.keys.stream().collect( Collectors.toMap( k -> k, this::bind ) );
	}

	private MethodHandle bind( MethodHandleKey<T> methodHandleKey )
	{
		MethodHandle methodHandle = methodHandleKey.methodHandle;
		methodHandle = methodHandle.asType( methodHandle.type().changeReturnType( Object.class ) );
		methodHandle = methodHandle.bindTo( exitPoint );
		methodHandle = methodHandle.asSpreader( Object[].class, methodHandleKey.method.getParameterCount() );
		return methodHandle;
	}

	final AnyCall<T> anycall = new AnyCall<>()
	{
		@Override public Object anyCall( MethodKey<T> key0, Object[] arguments )
		{
			MethodHandleKey<T> key = (MethodHandleKey<T>)key0;
			MethodHandle boundMethodHandle = Kit.map.get( boundMethodHandles, key );
			try
			{
				return Kit.invokeThrowableThrowingFunction( () -> boundMethodHandle.invokeExact( arguments ) );
			}
			catch( Throwable throwable )
			{
				Log.error( throwable );
				throw throwable;
			}
		}
	};
}
