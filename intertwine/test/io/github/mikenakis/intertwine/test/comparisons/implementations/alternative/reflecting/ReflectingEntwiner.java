package io.github.mikenakis.intertwine.test.comparisons.implementations.alternative.reflecting;

import io.github.mikenakis.intertwine.Anycall;
import io.github.mikenakis.intertwine.implementations.IntertwineHelpers;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Optional;

final class ReflectingEntwiner<T>
{
	private final ReflectingIntertwine<T> intertwine;
	private final Anycall<T> exitPoint;
	final T entryPoint;

	ReflectingEntwiner( ReflectingIntertwine<T> intertwine, Anycall<T> exitPoint )
	{
		this.intertwine = intertwine;
		assert exitPoint != null;
		this.exitPoint = exitPoint;
		ClassLoader classLoader = intertwine.interfaceType().getClassLoader();
		Class<?>[] interfaceTypes = { intertwine.interfaceType() };
		Object proxy = Proxy.newProxyInstance( classLoader, interfaceTypes, this::invocationHandler );
		@SuppressWarnings( "unchecked" ) T temp = (T)proxy;
		entryPoint = temp;
	}

	private Object invocationHandler( Object proxy, Method method, Object[] arguments )
	{
		assert proxy == entryPoint;
		Optional<ReflectingKey<T>> key = intertwine.tryGetKeyByMethod( method );
		if( key.isPresent() )
			return exitPoint.anycall( key.get(), arguments );
		if( method.equals( IntertwineHelpers.hashCodeMethod ) )
			return System.identityHashCode( proxy );
		if( method.equals( IntertwineHelpers.equalsMethod ) )
			return proxy == arguments[0];
		if( method.equals(IntertwineHelpers.toStringMethod ) )
			return Proxy.class.getName() + "@" + Integer.toHexString( System.identityHashCode( proxy ) );
		throw new AssertionError();
	}
}
