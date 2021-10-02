package mikenakis.test.intertwine.handwritten;

import mikenakis.intertwine.AnyCall;
import mikenakis.intertwine.Intertwine;
import mikenakis.kit.Kit;
import mikenakis.test.intertwine.rig.FooInterface;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Hand-written {@link Intertwine}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
class HandwrittenIntertwine implements Intertwine<FooInterface>
{
	private final List<HandwrittenKey> keys;
	private final Map<String,HandwrittenKey> keysBySignatureString;
	private final Map<Method,HandwrittenKey> keysByMethod;

	HandwrittenIntertwine()
	{
		Method[] methods = FooInterface.class.getMethods() ;
		keys = IntStream.range( 0, methods.length ).mapToObj( i -> createKey( i, methods[i] ) ).collect( Collectors.toList());
		keysBySignatureString = keys.stream().collect( Collectors.toMap( k -> k.signatureString, k -> k ) );
		keysByMethod = keys.stream().collect( Collectors.toMap( k -> k.method, k -> k ) );
	}

	private HandwrittenKey createKey( int index, Method method )
	{
		String signatureString = Intertwine.signatureString( method );
		return new HandwrittenKey( this, index, method, signatureString );
	}

	@Override public Class<FooInterface> interfaceType()
	{
		return FooInterface.class;
	}

	@Override public Collection<Key<FooInterface>> keys()
	{
		return Kit.collection.downCast( keys );
	}

	@Override public Key<FooInterface> keyByIndex( int id )
	{
		return keys.get( id );
	}

	@Override public Key<FooInterface> keyBySignatureString( String signatureString )
	{
		return Kit.map.get( keysBySignatureString, signatureString );
	}

	@Override public FooInterface newEntwiner( AnyCall<FooInterface> exitPoint )
	{
		return new HandwrittenEntwiner( this, exitPoint ).entryPoint;
	}

	@Override public AnyCall<FooInterface> newUntwiner( FooInterface exitPoint )
	{
		return new HandwrittenUntwiner( this, exitPoint ).anycall;
	}

	Optional<HandwrittenKey> tryGetKeyByMethod( Method method )
	{
		return Kit.map.getOptional( keysByMethod, method );
	}
}
