package io.github.mikenakis.intertwine.test.comparisons.rig.exchange.object;

import io.github.mikenakis.intertwine.Anycall;
import io.github.mikenakis.intertwine.Intertwine;
import io.github.mikenakis.intertwine.MethodKey;
import io.github.mikenakis.intertwine.test.comparisons.rig.exchange.ObjectExchange;

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
		MethodKey<T> key = intertwine.keyByMethod( request.method );
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
