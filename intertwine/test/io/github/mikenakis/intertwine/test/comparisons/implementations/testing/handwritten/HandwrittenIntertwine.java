package io.github.mikenakis.intertwine.test.comparisons.implementations.testing.handwritten;

import io.github.mikenakis.intertwine.Anycall;
import io.github.mikenakis.intertwine.Intertwine;
import io.github.mikenakis.intertwine.MethodKey;
import io.github.mikenakis.intertwine.test.comparisons.rig.FooInterface;
import io.github.mikenakis.kit.Kit;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Handwritten {@link Intertwine} for a specific interface: {@link FooInterface}.
 *
 * @author michael.gr
 */
class HandwrittenIntertwine implements Intertwine<FooInterface>
{
	private final HandwrittenKey[] keys;
	private final Map<Method,HandwrittenKey> keysByMethod;

	HandwrittenIntertwine()
	{
		Method[] methods = FooInterface.class.getMethods();
		keys = IntStream.range( 0, methods.length ).mapToObj( i -> createKey( i, methods[i] ) ).toArray( HandwrittenKey[]::new );
		keysByMethod = Stream.of( keys ).collect( Collectors.toMap( k -> k.method, k -> k ) );
	}

	private HandwrittenKey createKey( int index, Method method )
	{
		return new HandwrittenKey( this, index, method );
	}

	@Override public Class<FooInterface> interfaceType()
	{
		return FooInterface.class;
	}

	@Override public boolean implementsDefaultMethods()
	{
		return false;
	}

	@Override public List<MethodKey<FooInterface>> keys()
	{
		return List.of( keys );
	}

	@Override public MethodKey<FooInterface> keyByMethod( Method method )
	{
		return Kit.map.get( keysByMethod, method );
	}

	@Override public FooInterface newEntwiner( Anycall<FooInterface> exitPoint )
	{
		return new HandwrittenEntwiner( keys, exitPoint );
	}

	@Override public Anycall<FooInterface> newUntwiner( FooInterface exitPoint )
	{
		return new HandwrittenUntwiner( exitPoint );
	}
}
