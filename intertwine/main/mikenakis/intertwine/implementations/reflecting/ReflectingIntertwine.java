package mikenakis.intertwine.implementations.reflecting;

import mikenakis.bytecode.model.descriptors.MethodPrototype;
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
 * Reflecting {@link Intertwine}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
class ReflectingIntertwine<T> implements Intertwine<T>
{
	private final Class<? super T> interfaceType;
	private final List<ReflectingKey<T>> keys;
	private final Map<MethodPrototype,ReflectingKey<T>> keysByPrototype;
	private final Map<Method,ReflectingKey<T>> keysByMethod;

	ReflectingIntertwine( Class<? super T> interfaceType )
	{
		assert interfaceType.isInterface();
		if( !Modifier.isPublic( interfaceType.getModifiers() ) )
			throw new RuntimeException( new IllegalAccessException() );
		this.interfaceType = interfaceType;
		Method[] methods = interfaceType.getMethods();
		keys = IntStream.range( 0, methods.length ).mapToObj( i -> createKey( methods[i], i ) ).collect( Collectors.toList());
		keysByPrototype = keys.stream().collect( Collectors.toMap( k -> k.methodPrototype, k -> k ) );
		keysByMethod = keys.stream().collect( Collectors.toMap( k -> k.method, k -> k ) );
	}

	private ReflectingKey<T> createKey( Method method, int index )
	{
		MethodPrototype methodPrototype = MethodPrototype.of( method );
		return new ReflectingKey<>( this, method, methodPrototype, index );
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

	@Override public Key<T> keyByMethodPrototype( MethodPrototype methodPrototype )
	{
		return Kit.map.get( keysByPrototype, methodPrototype );
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
