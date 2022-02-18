package mikenakis.test.intertwine.comparisons.rig.exchange.object;

import mikenakis.intertwine.MethodKey;
import mikenakis.test.intertwine.comparisons.rig.exchange.ObjectExchange;
import mikenakis.intertwine.Anycall;
import mikenakis.kit.Kit;

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
