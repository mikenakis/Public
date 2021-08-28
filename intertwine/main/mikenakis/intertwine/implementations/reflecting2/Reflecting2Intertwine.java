package mikenakis.intertwine.implementations.reflecting2;

import mikenakis.intertwine.AnyCall;
import mikenakis.intertwine.Intertwine;
import mikenakis.kit.Kit;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Reflecting {@link Intertwine}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
class Reflecting2Intertwine<T> implements Intertwine<T>
{
	private final Class<? super T> interfaceType;
	private final List<Reflecting2Key<T>> keys;
	private final Map<String,Reflecting2Key<T>> keysBySignatureString;
	private final Map<Method,Reflecting2Key<T>> keysByMethod;

	Reflecting2Intertwine( Class<? super T> interfaceType )
	{
		assert interfaceType.isInterface();
		if( !Modifier.isPublic( interfaceType.getModifiers() ) )
			throw new RuntimeException( new IllegalAccessException() );
		this.interfaceType = interfaceType;
		List<Method> methods = List.of( interfaceType.getMethods() );
		keys = methods.stream().map( m -> createKey( m ) ).collect( Collectors.toList());
		keysBySignatureString = keys.stream().collect( Collectors.toMap( k -> k.signatureString, k -> k ) );
		keysByMethod = keys.stream().collect( Collectors.toMap( k -> k.method, k -> k ) );
	}

	private Reflecting2Key<T> createKey( Method method )
	{
		String signatureString = Intertwine.signatureString( method );
		return new Reflecting2Key<>( this, method, signatureString );
	}

	@Override public Class<? super T> interfaceType()
	{
		return interfaceType;
	}

	@Override public Collection<Key<T>> keys()
	{
		return Kit.collection.downCast( keys );
	}

	@Override public Key<T> keyById( int id )
	{
		return keys.get( id );
	}

	@Override public Key<T> keyBySignatureString( String signatureString )
	{
		return Kit.map.get( keysBySignatureString, signatureString );
	}

	@Override public T newEntwiner( AnyCall<T> exitPoint )
	{
		return new Reflecting2Entwiner<>( this, exitPoint ).entryPoint;
	}

	@Override public AnyCall<T> newUntwiner( T exitPoint )
	{
		return new Reflecting2Untwiner<>( this, exitPoint ).anycall;
	}

	Optional<Reflecting2Key<T>> tryGetKeyByMethod( Method method )
	{
		return Kit.map.getOptional( keysByMethod, method );
	}
}
