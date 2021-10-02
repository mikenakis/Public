package mikenakis.intertwine.implementations.reflecting;

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
class ReflectingIntertwine<T> implements Intertwine<T>
{
	private final Class<? super T> interfaceType;
	private final List<ReflectingKey<T>> keys;
	private final Map<String,ReflectingKey<T>> keysBySignatureString;
	private final Map<Method,ReflectingKey<T>> keysByMethod;

	ReflectingIntertwine( Class<? super T> interfaceType )
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

	private ReflectingKey<T> createKey( Method method )
	{
		String signatureString = Intertwine.signatureString( method );
		return new ReflectingKey<>( this, method, signatureString );
	}

	@Override public Class<? super T> interfaceType()
	{
		return interfaceType;
	}

	@Override public Collection<Key<T>> keys()
	{
		return Kit.collection.downCast( keys );
	}

	@Override public Key<T> keyByIndex( int id )
	{
		return keys.get( id );
	}

	@Override public Key<T> keyBySignatureString( String signatureString )
	{
		return Kit.map.get( keysBySignatureString, signatureString );
	}

	@Override public T newEntwiner( AnyCall<T> exitPoint )
	{
		return new ReflectingEntwiner<>( this, exitPoint ).entryPoint;
	}

	@Override public AnyCall<T> newUntwiner( T exitPoint )
	{
		return new ReflectingUntwiner<>( this, exitPoint ).anycall;
	}

	Optional<ReflectingKey<T>> tryGetKeyByMethod( Method method )
	{
		return Kit.map.getOptional( keysByMethod, method );
	}
}
