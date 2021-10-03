package mikenakis.intertwine.multicast;

import mikenakis.intertwine.AnyCall;
import mikenakis.intertwine.Intertwine;
import mikenakis.intertwine.IntertwineFactory;
import mikenakis.intertwine.MethodKey;
import mikenakis.kit.Kit;
import mikenakis.kit.ExecutionContext;
import mikenakis.multicast.Multicast;

import java.util.LinkedHashMap;
import java.util.Map;

public class InterfaceMulticaster<T>
{
	private final ExecutionContext executionContext;
	private final Map<T,AnyCall<T>> observers = new LinkedHashMap<>();
	private final Intertwine<T> intertwine;
	private final T entryPoint;
	public final Multicast<T> multicast = new Multicast.Defaults<>()
	{
		@Override public void add( T observer )
		{
			AnyCall<T> anyCall = intertwine.newUntwiner( observer );
			Kit.map.add( observers, observer, anyCall );
		}

		@Override public void remove( T observer )
		{
			Kit.map.remove( observers, observer );
		}

		@Override public boolean contains( T observer )
		{
			return observers.containsKey( observer );
		}
	};
	@SuppressWarnings( "FieldCanBeLocal" ) private final AnyCall<T> anyCall = new AnyCall<>()
	{
		@Override public Object anyCall( MethodKey<T> key, Object[] arguments )
		{
			for( AnyCall<T> anyCall : observers.values().stream().toList() )
			{
				Object result = anyCall.anyCall( key, arguments );
				assert result == null;
			}
			return null;
		}
	};

	public InterfaceMulticaster( ExecutionContext executionContext, IntertwineFactory intertwineFactory, Class<T> interfaceType )
	{
		assert executionContext.inContextAssertion();
		this.executionContext = executionContext;
		intertwine = intertwineFactory.getIntertwine( interfaceType );
		entryPoint = intertwine.newEntwiner( anyCall );
	}

	public T entryPoint()
	{
		assert executionContext.inContextAssertion();
		return entryPoint;
	}

	public boolean isEmpty()
	{
		return observers.isEmpty();
	}
}
