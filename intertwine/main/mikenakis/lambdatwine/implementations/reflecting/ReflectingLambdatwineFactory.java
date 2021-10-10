package mikenakis.lambdatwine.implementations.reflecting;

import mikenakis.lambdatwine.Lambdatwine;
import mikenakis.lambdatwine.LambdatwineFactory;

/**
 * A {@link LambdatwineFactory} for {@link ReflectingLambdatwine}.
 *
 * @author michael.gr
 */
public class ReflectingLambdatwineFactory implements LambdatwineFactory
{
	public ReflectingLambdatwineFactory()
	{
	}

	@Override public <T> Lambdatwine<T> getLambdatwine( Class<T> interfaceType )
	{
		return new ReflectingLambdatwine<>( interfaceType );
	}
}
