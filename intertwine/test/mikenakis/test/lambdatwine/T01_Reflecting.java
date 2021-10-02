package mikenakis.test.lambdatwine;

import mikenakis.lambdatwine.LambdatwineFactory;
import mikenakis.lambdatwine.implementations.reflecting.ReflectingLambdatwineFactory;

public final class T01_Reflecting extends Client
{
	public T01_Reflecting()
	{
	}

	private final LambdatwineFactory lambdatwineFactory = new ReflectingLambdatwineFactory();

	@Override protected LambdatwineFactory getLambdatwineFactory()
	{
		return lambdatwineFactory;
	}
}
