package mikenakis.intertwine.implementations.methodhandle;

import mikenakis.intertwine.AnyCall;
import mikenakis.intertwine.Intertwine;
import mikenakis.kit.Kit;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Method Handle {@link Intertwine}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
class MethodHandleIntertwine<T> implements Intertwine<T>
{
	private final Class<? super T> interfaceType;
	final List<MethodHandleKey<T>> keys;
	private final Map<String,MethodHandleKey<T>> keysBySignatureString;
	private final Map<Method,MethodHandleKey<T>> keysByMethod;

	MethodHandleIntertwine( Class<? super T> interfaceType )
	{
		assert interfaceType.isInterface();
		if( !Modifier.isPublic( interfaceType.getModifiers() ) )
			throw new RuntimeException( new IllegalAccessException() );
		this.interfaceType = interfaceType;
		MethodHandles.Lookup lookup = MethodHandles.publicLookup().in( interfaceType );
		List<Method> methods = List.of( interfaceType.getMethods() );
		keys = methods.stream().map( m -> createKey( lookup, m ) ).collect( Collectors.toList() );
		keysBySignatureString = keys.stream().collect( Collectors.toMap( k -> k.signatureString, k -> k ) );
		keysByMethod = keys.stream().collect( Collectors.toMap( k -> k.method, k -> k ) );
	}

	private MethodHandleKey<T> createKey( MethodHandles.Lookup lookup, Method method )
	{
		String signatureString = Intertwine.signatureString( method );
		MethodHandle methodHandle = Kit.unchecked( () -> lookup.unreflect( method ) );
		return new MethodHandleKey<>( this, method, signatureString, methodHandle );
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
		return new MethodHandleEntwiner<>( this, exitPoint ).entryPoint;
	}

	@Override public AnyCall<T> newUntwiner( T exitPoint )
	{
		return new MethodHandleUntwiner<>( this, exitPoint ).anycall;
	}

	Optional<MethodHandleKey<T>> tryGetKeyByMethod( Method method )
	{
		return Kit.map.getOptional( keysByMethod, method );
	}
}
