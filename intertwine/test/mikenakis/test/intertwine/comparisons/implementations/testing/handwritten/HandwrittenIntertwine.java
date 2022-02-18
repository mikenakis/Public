package mikenakis.test.intertwine.comparisons.implementations.testing.handwritten;

import mikenakis.bytecode.model.descriptors.MethodPrototype;
import mikenakis.intertwine.Anycall;
import mikenakis.intertwine.Intertwine;
import mikenakis.intertwine.MethodKey;
import mikenakis.kit.Kit;
import mikenakis.test.intertwine.comparisons.rig.FooInterface;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Hand-written {@link Intertwine} for a specific interface: {@link FooInterface}.
 *
 * @author michael.gr
 */
class HandwrittenIntertwine implements Intertwine<FooInterface>
{
	private final HandwrittenKey[] keys;
	private final Map<MethodPrototype,HandwrittenKey> keysByPrototype;

	HandwrittenIntertwine()
	{
		Method[] methods = FooInterface.class.getMethods() ;
		keys = IntStream.range( 0, methods.length ).mapToObj( i -> createKey( i, methods[i] ) ).toArray( HandwrittenKey[]::new );
		keysByPrototype = Stream.of( keys ).collect( Collectors.toMap( k -> k.methodPrototype, k -> k ) );
	}

	private HandwrittenKey createKey( int index, Method method )
	{
		MethodPrototype methodPrototype = MethodPrototype.of( method );
		return new HandwrittenKey( this, index, methodPrototype );
	}

	@Override public Class<FooInterface> interfaceType()
	{
		return FooInterface.class;
	}

	@Override public Collection<MethodKey<FooInterface>> keys()
	{
		return Kit.collection.downCast( List.of( keys ) );
	}

	@Override public MethodKey<FooInterface> keyByIndex( int index )
	{
		return keys[index];
	}

	@Override public MethodKey<FooInterface> keyByMethodPrototype( MethodPrototype methodPrototype )
	{
		return Kit.map.get( keysByPrototype, methodPrototype );
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
