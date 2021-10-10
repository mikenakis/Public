package mikenakis.lambdatwine.implementations.reflecting;

import mikenakis.lambdatwine.AnyLambda;
import mikenakis.lambdatwine.Lambdatwine;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * Reflecting {@link Lambdatwine}.
 *
 * @author michael.gr
 */
class ReflectingLambdatwine<T> implements Lambdatwine<T>
{
	private final Class<T> interfaceType;
	final Method method;

	ReflectingLambdatwine( Class<T> interfaceType )
	{
		assert interfaceType.isInterface();
		assert Modifier.isPublic( interfaceType.getModifiers() ) : new IllegalAccessException();
		List<Method> methods = List.of( interfaceType.getMethods() );
		assert methods.size() == 1;
		this.interfaceType = interfaceType;
		method = methods.get( 0 );
	}

	@Override public Class<T> interfaceType()
	{
		return interfaceType;
	}

	@Override public T newEntwiner( AnyLambda<T> exitPoint )
	{
		return new ReflectingLambdaEntwiner<>( this, exitPoint ).entryPoint;
	}

	@Override public AnyLambda<T> newUntwiner( T exitPoint )
	{
		return new ReflectingLambdaUntwiner<>( this, exitPoint ).anyLambda;
	}
}
