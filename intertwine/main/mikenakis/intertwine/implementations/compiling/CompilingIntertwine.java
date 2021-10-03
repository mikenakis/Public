package mikenakis.intertwine.implementations.compiling;

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
import java.util.stream.IntStream;

/**
 * Compiling {@link Intertwine}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
class CompilingIntertwine<T> implements Intertwine<T>
{
	private final Class<? super T> interfaceType;
	private final List<CompilingKey<T>> keys;
	private final Map<String,CompilingKey<T>> keysByPrototypeString;
	private final Map<Method,CompilingKey<T>> keysByMethod;

	CompilingIntertwine( Class<? super T> interfaceType )
	{
		assert interfaceType.isInterface();
		if( !Modifier.isPublic( interfaceType.getModifiers() ) )
			throw new RuntimeException( new IllegalAccessException() );
		this.interfaceType = interfaceType;
		Method[] methods = interfaceType.getMethods();
		keys = IntStream.range( 0, methods.length ).mapToObj( i -> createKey( methods[i], i ) ).collect( Collectors.toList());
		keysByPrototypeString = keys.stream().collect( Collectors.toMap( k -> k.prototypeString, k -> k ) );
		keysByMethod = keys.stream().collect( Collectors.toMap( k -> k.method, k -> k ) );
	}

	private CompilingKey<T> createKey( Method method, int index )
	{
		String prototypeString = Intertwine.prototypeString( method );
		return new CompilingKey<>( this, method, prototypeString, index );
	}

	@Override public Class<? super T> interfaceType()
	{
		return interfaceType;
	}

	@Override public Collection<Key<T>> keys()
	{
		return Kit.collection.downCast( keys );
	}

	@Override public Key<T> keyByIndex( int index )
	{
		return keys.get( index );
	}

	@Override public Key<T> keyByPrototypeString( String prototypeString )
	{
		return Kit.map.get( keysByPrototypeString, prototypeString );
	}

	@Override public T newEntwiner( AnyCall<T> exitPoint )
	{
		return new CompilingEntwiner<>( this, exitPoint ).entryPoint;
	}

	@Override public AnyCall<T> newUntwiner( T exitPoint )
	{
		return new CompilingUntwiner<>( this, exitPoint ).anycall;
	}

	Optional<CompilingKey<T>> tryGetKeyByMethod( Method method )
	{
		return Kit.map.getOptional( keysByMethod, method );
	}
}
