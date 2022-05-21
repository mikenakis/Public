package io.github.mikenakis.intertwine.test.comparisons.rig.exchange.object;

import io.github.mikenakis.intertwine.Anycall;
import io.github.mikenakis.intertwine.MethodKey;
import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.intertwine.test.comparisons.rig.exchange.ObjectExchange;

public class AnycallToObjectExchange<T> implements Anycall<T>
{
	private final ObjectExchange<AnycallResponse,AnycallRequest> objectExchange;

	public AnycallToObjectExchange( ObjectExchange<AnycallResponse,AnycallRequest> objectExchange )
	{
		this.objectExchange = objectExchange;
	}

	@Override public Object anycall( MethodKey<T> key, Object[] arguments )
	{
		AnycallRequest anycallRequest = new AnycallRequest( key.methodPrototype(), arguments );
		AnycallResponse anycallResponse = objectExchange.doExchange( anycallRequest );
		if( !anycallResponse.success )
			throw Kit.sneakyException( anycallResponse.throwable() );
		return anycallResponse.payload();
	}
}
