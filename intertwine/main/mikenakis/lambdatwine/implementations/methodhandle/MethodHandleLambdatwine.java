package mikenakis.lambdatwine.implementations.methodhandle;

import mikenakis.kit.Kit;
import mikenakis.lambdatwine.AnyLambda;
import mikenakis.lambdatwine.Lambdatwine;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * Method Handle {@link Lambdatwine}.
 *
 * @author michael.gr
 */
class MethodHandleLambdatwine<T> implements Lambdatwine<T>
{
	private final Class<T> interfaceType;
	final Method method;
	final MethodHandle methodHandle;

	MethodHandleLambdatwine( MethodHandleLambdatwineFactory factory, Class<T> interfaceType )
	{
		assert interfaceType != null;
		assert interfaceType.isInterface();
		assert Modifier.isPublic( interfaceType.getModifiers() ) : new IllegalAccessException();
		List<Method> methods = List.of( interfaceType.getMethods() );
		assert methods.size() == 1;
		this.interfaceType = interfaceType;
		MethodHandles.Lookup lookup = MethodHandles.publicLookup().in( interfaceType );
		method = methods.get( 0 );
		methodHandle = Kit.unchecked( () -> lookup.unreflect( method ) );
	}

	@Override public Class<T> interfaceType()
	{
		return interfaceType;
	}

	@Override public T newEntwiner( AnyLambda<T> exitPoint )
	{
		return new MethodHandleLambdaEntwiner<>( this, exitPoint ).entryPoint;
	}

	@Override public AnyLambda<T> newUntwiner( T exitPoint )
	{
		return new MethodHandleLambdaUntwiner<>( this, exitPoint ).anyLambda;
	}
}
