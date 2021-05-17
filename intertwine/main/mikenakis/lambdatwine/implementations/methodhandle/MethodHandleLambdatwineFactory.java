package mikenakis.lambdatwine.implementations.methodhandle;

import mikenakis.lambdatwine.Lambdatwine;
import mikenakis.lambdatwine.LambdatwineFactory;

/**
 * A {@link LambdatwineFactory} for {@link MethodHandleLambdatwine}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class MethodHandleLambdatwineFactory implements LambdatwineFactory
{
	public MethodHandleLambdatwineFactory()
	{
	}

	@Override public <T> Lambdatwine<T> getLambdatwine( Class<T> interfaceType )
	{
		return new MethodHandleLambdatwine<>( this, interfaceType );
	}
}
