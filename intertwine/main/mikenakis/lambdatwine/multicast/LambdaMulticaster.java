package mikenakis.lambdatwine.multicast;

import mikenakis.kit.Kit;
import mikenakis.kit.ThreadGuard;
import mikenakis.lambdatwine.AnyLambda;
import mikenakis.lambdatwine.Lambdatwine;
import mikenakis.lambdatwine.LambdatwineFactory;
import mikenakis.multicast.Multicast;

import java.util.LinkedHashMap;
import java.util.Map;

public class LambdaMulticaster<T>
{
	private final ThreadGuard threadGuard = ThreadGuard.create();
	private final Map<T,AnyLambda<T>> observers = new LinkedHashMap<>();
	private final Lambdatwine<T> lambdatwine;
	private final T entryPoint;
	public final Multicast<T> multicast = new Multicast.Defaults<>()
	{
		@Override public void add( T observer )
		{
			AnyLambda<T> anyLambda = lambdatwine.newUntwiner( observer );
			Kit.map.add( observers, observer, anyLambda );
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
	@SuppressWarnings( "FieldCanBeLocal" ) private final AnyLambda<T> anyLambda = new AnyLambda<>()
	{
		@Override public Object anyLambda( Object[] arguments )
		{
			for( AnyLambda<T> anyLambda : observers.values().stream().toList() )
			{
				Object result = anyLambda.anyLambda( arguments );
				assert result == null;
			}
			return null;
		}
	};

	public LambdaMulticaster( LambdatwineFactory lambdatwineFactory, Class<T> lambdaType )
	{
		lambdatwine = lambdatwineFactory.getLambdatwine( lambdaType );
		entryPoint = lambdatwine.newEntwiner( anyLambda );
	}

	public T entryPoint()
	{
		assert threadGuard.inThreadAssertion();
		return entryPoint;
	}
}
