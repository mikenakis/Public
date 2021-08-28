package mikenakis.intertwine.implementations.reflecting2;

import mikenakis.bytecode.ByteCodeField;
import mikenakis.bytecode.ByteCodeType;
import mikenakis.intertwine.AnyCall;
import mikenakis.intertwine.implementations.IntertwineHelpers;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.EnumSet;
import java.util.Optional;

final class Reflecting2Entwiner<T>
{
	private final Reflecting2Intertwine<T> intertwine;
	private final AnyCall<T> exitPoint;
	final T entryPoint;

	Reflecting2Entwiner( Reflecting2Intertwine<T> intertwine, AnyCall<T> exitPoint )
	{
		this.intertwine = intertwine;
		assert exitPoint != null;
		this.exitPoint = exitPoint;
		ClassLoader classLoader = intertwine.interfaceType().getClassLoader();
		Class<?>[] interfaceTypes = { intertwine.interfaceType() };
		Object proxy = Proxy.newProxyInstance( classLoader, interfaceTypes, this::invocationHandler );
		//Object proxy = newProxyInstance( classLoader, interfaceTypes, this::invocationHandler );
		@SuppressWarnings( "unchecked" ) T temp = (T)proxy;
		entryPoint = temp;
	}

	private Object invocationHandler( Object proxy, Method method, Object[] arguments )
	{
		assert proxy == entryPoint;
		Optional<Reflecting2Key<T>> key = intertwine.tryGetKeyByMethod( method );
		if( key.isPresent() )
			return exitPoint.anyCall( key.get(), arguments );
		if( method.equals( IntertwineHelpers.hashCodeMethod ) )
			return System.identityHashCode( proxy );
		if( method.equals( IntertwineHelpers.equalsMethod ) )
			return proxy == arguments[0];
		if( method.equals( IntertwineHelpers.toStringMethod ) )
			return Proxy.class.getName() + "@" + Integer.toHexString( System.identityHashCode( proxy ) );
		throw new AssertionError();
	}

	private static Object newProxyInstance( ClassLoader loader, Class<?>[] interfaces, InvocationHandler handler )
	{
		String className = "test";
		Optional<String> superClassName = Optional.empty();
		ByteCodeType byteCodeType = ByteCodeType.create( EnumSet.noneOf( ByteCodeType.Access.class ), className, superClassName );
		ByteCodeField byteCodeField = new ByteCodeField( () -> { assert false; }, byteCodeType, EnumSet.noneOf( ByteCodeField.Access.class ), "handler", "" );
		byteCodeType.fields.add( byteCodeField );
		return byteCodeType;
	}
}
