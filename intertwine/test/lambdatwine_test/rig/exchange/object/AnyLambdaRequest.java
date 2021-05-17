package lambdatwine_test.rig.exchange.object;

public final class AnyLambdaRequest
{
	public final Object[] arguments;

	public AnyLambdaRequest( Object[] arguments )
	{
		this.arguments = arguments;
	}

	@Override public String toString()
	{
		return arguments.length + " arguments";
	}
}
