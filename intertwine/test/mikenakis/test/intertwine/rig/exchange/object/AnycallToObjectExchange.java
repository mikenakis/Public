package mikenakis.test.intertwine.rig.exchange.object;

import mikenakis.test.intertwine.rig.exchange.ObjectExchange;
import mikenakis.intertwine.AnyCall;
import mikenakis.intertwine.Intertwine;
import mikenakis.kit.Kit;

public class AnycallToObjectExchange<T> implements AnyCall<T>
{
	private final ObjectExchange<AnycallResponse,AnycallRequest> objectExchange;

	public AnycallToObjectExchange( ObjectExchange<AnycallResponse,AnycallRequest> objectExchange )
	{
		this.objectExchange = objectExchange;
	}

	@Override public Object anyCall( Intertwine.Key<T> key, Object[] arguments )
	{
		AnycallRequest anycallRequest = new AnycallRequest( key.getPrototypeString(), arguments );
		AnycallResponse anycallResponse = objectExchange.doExchange( anycallRequest );
		if( !anycallResponse.success )
			throw Kit.sneakyException( anycallResponse.throwable() );
		return anycallResponse.payload();
	}
}
