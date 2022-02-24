package mikenakis.test.intertwine.comparisons.implementations.alternative.methodhandle;

import mikenakis.intertwine.Anycall;
import mikenakis.intertwine.MethodKey;
import mikenakis.kit.Kit;

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

	final Anycall<T> anycall = new Anycall<>()
	{
		@Override public Object anycall( MethodKey<T> key0, Object[] arguments )
		{
			MethodHandleKey<T> key = (MethodHandleKey<T>)key0;
			MethodHandle boundMethodHandle = Kit.map.get( boundMethodHandles, key );
			return Kit.invokeThrowableThrowingFunction( () -> boundMethodHandle.invokeExact( arguments ) );
		}
	};
}
