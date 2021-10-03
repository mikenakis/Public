package mikenakis.test.intertwine.rig.exchange.object;

import mikenakis.intertwine.MethodKey;
import mikenakis.test.intertwine.rig.exchange.ObjectExchange;
import mikenakis.intertwine.AnyCall;
import mikenakis.kit.Kit;

public class AnycallToObjectExchange<T> implements AnyCall<T>
{
	private final ObjectExchange<AnycallResponse,AnycallRequest> objectExchange;

	public AnycallToObjectExchange( ObjectExchange<AnycallResponse,AnycallRequest> objectExchange )
	{
		this.objectExchange = objectExchange;
	}

	@Override public Object anyCall( MethodKey<T> key, Object[] arguments )
	{
		AnycallRequest anycallRequest = new AnycallRequest( key.methodPrototype(), arguments );
		AnycallResponse anycallResponse = objectExchange.doExchange( anycallRequest );
		if( !anycallResponse.success )
			throw Kit.sneakyException( anycallResponse.throwable() );
		return anycallResponse.payload();
	}
}
