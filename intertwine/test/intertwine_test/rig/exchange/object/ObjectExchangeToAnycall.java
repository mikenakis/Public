package intertwine_test.rig.exchange.object;

import intertwine_test.rig.exchange.ObjectExchange;
import mikenakis.intertwine.AnyCall;
import mikenakis.intertwine.Intertwine;

public class ObjectExchangeToAnycall<T> implements ObjectExchange<AnycallResponse,AnycallRequest>
{
	private final Intertwine<T> intertwine;
	private final AnyCall<T> anycall;

	public ObjectExchangeToAnycall( Intertwine<T> intertwine, AnyCall<T> anycall )
	{
		this.intertwine = intertwine;
		this.anycall = anycall;
	}

	@Override public AnycallResponse doExchange( AnycallRequest request )
	{
		Intertwine.Key<T> key = intertwine.keyBySignatureString( request.signatureString );
		try
		{
			Object result = anycall.anyCall( key, request.arguments );
			return AnycallResponse.success( result );
		}
		catch( Throwable throwable )
		{
			return AnycallResponse.failure( throwable );
		}
	}
}
