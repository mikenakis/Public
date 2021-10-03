package mikenakis.test.intertwine.handwritten;

import mikenakis.bytecode.model.descriptors.MethodPrototype;
import mikenakis.intertwine.AnyCall;
import mikenakis.intertwine.Intertwine;
import mikenakis.kit.Kit;
import mikenakis.test.intertwine.rig.FooInterface;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Hand-written {@link Intertwine} for a specific interface: {@link FooInterface}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
class HandwrittenIntertwine implements Intertwine<FooInterface>
{
	private final List<HandwrittenKey> keys;
	private final Map<MethodPrototype,HandwrittenKey> keysByPrototype;

	HandwrittenIntertwine()
	{
		Method[] methods = FooInterface.class.getMethods() ;
		keys = IntStream.range( 0, methods.length ).mapToObj( i -> createKey( i, methods[i] ) ).collect( Collectors.toList());
		keysByPrototype = keys.stream().collect( Collectors.toMap( k -> k.methodPrototype, k -> k ) );
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

	@Override public Collection<Key<FooInterface>> keys()
	{
		return Kit.collection.downCast( keys );
	}

	@Override public Key<FooInterface> keyByIndex( int index )
	{
		return keys.get( index );
	}

	@Override public Key<FooInterface> keyByMethodPrototype( MethodPrototype methodPrototype )
	{
		return Kit.map.get( keysByPrototype, methodPrototype );
	}

	@Override public FooInterface newEntwiner( AnyCall<FooInterface> exitPoint )
	{
		return new HandwrittenEntwiner( this, exitPoint ).entryPoint;
	}

	@Override public AnyCall<FooInterface> newUntwiner( FooInterface exitPoint )
	{
		return new HandwrittenUntwiner( this, exitPoint ).anycall;
	}
}
