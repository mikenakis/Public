package mikenakis.intertwine.implementations.methodhandle;

import mikenakis.intertwine.AnyCall;
import mikenakis.intertwine.implementations.IntertwineHelpers;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Optional;

final class MethodHandleEntwiner<T>
{
	private final MethodHandleIntertwine<T> intertwine;
	private final AnyCall<T> exitPoint;
	final T entryPoint;

	MethodHandleEntwiner( MethodHandleIntertwine<T> intertwine, AnyCall<T> exitPoint )
	{
		this.intertwine = intertwine;
		this.exitPoint = exitPoint;
		ClassLoader classLoader = intertwine.interfaceType().getClassLoader();
		Class<?>[] interfaceTypes = { intertwine.interfaceType() };
		Object proxy = Proxy.newProxyInstance( classLoader, interfaceTypes, this::invocationHandler );
		@SuppressWarnings( "unchecked" ) T temp = (T)proxy;
		entryPoint = temp;
	}

	private Object invocationHandler( Object proxy, Method method, Object[] arguments )
	{
		Optional<MethodHandleKey<T>> key = intertwine.tryGetKeyByMethod( method );
		if( key.isPresent() )
			return exitPoint.anyCall( key.get(), arguments );
		if( method.equals( IntertwineHelpers.hashCodeMethod ) )
			return System.identityHashCode( proxy );
		if( method.equals( IntertwineHelpers.equalsMethod ) )
			return proxy == arguments[0];
		if( method.equals(IntertwineHelpers.toStringMethod ) )
			return Proxy.class.getName() + "@" + Integer.toHexString( System.identityHashCode( proxy ) );
		throw new AssertionError();
	}
}
