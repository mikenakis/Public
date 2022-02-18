package mikenakis.test.intertwine.comparisons.rig.exchange.object;

import mikenakis.intertwine.MethodKey;
import mikenakis.test.intertwine.comparisons.rig.exchange.ObjectExchange;
import mikenakis.intertwine.Anycall;
import mikenakis.intertwine.Intertwine;

public class ObjectExchangeToAnycall<T> implements ObjectExchange<AnycallResponse,AnycallRequest>
{
	private final Intertwine<T> intertwine;
	private final Anycall<T> anycall;

	public ObjectExchangeToAnycall( Intertwine<T> intertwine, Anycall<T> anycall )
	{
		this.intertwine = intertwine;
		this.anycall = anycall;
	}

	@Override public AnycallResponse doExchange( AnycallRequest request )
	{
		MethodKey<T> key = intertwine.keyByMethodPrototype( request.methodPrototype );
		try
		{
			Object result = anycall.anycall( key, request.arguments );
			return AnycallResponse.success( result );
		}
		catch( Throwable throwable )
		{
			return AnycallResponse.failure( throwable );
		}
	}
}
