package mikenakis.lambdatwine.implementations.methodhandle;

import mikenakis.lambdatwine.Lambdatwine;
import mikenakis.lambdatwine.LambdatwineFactory;

/**
 * A {@link LambdatwineFactory} for {@link MethodHandleLambdatwine}.
 *
 * @author michael.gr
 */
public class MethodHandleLambdatwineFactory implements LambdatwineFactory
{
	public static final LambdatwineFactory instance = new MethodHandleLambdatwineFactory();

	private MethodHandleLambdatwineFactory()
	{
	}

	@Override public <T> Lambdatwine<T> getLambdatwine( Class<T> interfaceType )
	{
		return new MethodHandleLambdatwine<>( this, interfaceType );
	}
}
