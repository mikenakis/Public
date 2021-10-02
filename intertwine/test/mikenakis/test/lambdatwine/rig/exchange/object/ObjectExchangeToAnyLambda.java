package mikenakis.test.lambdatwine.rig.exchange.object;

import mikenakis.test.lambdatwine.rig.exchange.ObjectExchange;
import mikenakis.lambdatwine.AnyLambda;

public class ObjectExchangeToAnyLambda<T> implements ObjectExchange<AnyLambdaResponse,AnyLambdaRequest>
{
	private final AnyLambda<T> anyLambda;

	public ObjectExchangeToAnyLambda( AnyLambda<T> anyLambda )
	{
		this.anyLambda = anyLambda;
	}

	@Override public AnyLambdaResponse doExchange( AnyLambdaRequest request )
	{
		try
		{
			Object result = anyLambda.anyLambda( request.arguments );
			return AnyLambdaResponse.success( result );
		}
		catch( Throwable throwable )
		{
			return AnyLambdaResponse.failure( throwable );
		}
	}
}
