package lambdatwine_test;

import mikenakis.lambdatwine.LambdatwineFactory;
import mikenakis.lambdatwine.implementations.methodhandle.MethodHandleLambdatwineFactory;

public final class T02_MethodHandle extends Client
{
	public T02_MethodHandle()
	{
	}

	@Override protected LambdatwineFactory getLambdatwineFactory()
	{
		return MethodHandleLambdatwineFactory.instance;
	}
}
