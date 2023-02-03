package io.github.mikenakis.intertwine.test.comparisons.implementations.alternative.reflecting;

import io.github.mikenakis.intertwine.Anycall;
import io.github.mikenakis.intertwine.Intertwine;
import io.github.mikenakis.intertwine.MethodKey;
import io.github.mikenakis.kit.Kit;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Reflecting {@link Intertwine}.
 *
 * @author michael.gr
 */
class ReflectingIntertwine<T> implements Intertwine<T>
{
	private final Class<? super T> interfaceType;
	private final List<ReflectingKey<T>> keys;
	private final Map<Method,ReflectingKey<T>> keysByMethod;

	ReflectingIntertwine( Class<? super T> interfaceType )
	{
		assert interfaceType.isInterface();
		assert Modifier.isPublic( interfaceType.getModifiers() ) : new IllegalAccessException();
		this.interfaceType = interfaceType;
		Method[] methods = interfaceType.getMethods();
		keys = IntStream.range( 0, methods.length ).mapToObj( i -> createKey( methods[i], i ) ).toList();
		keysByMethod = keys.stream().collect( Collectors.toMap( k -> k.method, k -> k ) );
	}

	private ReflectingKey<T> createKey( Method method, int index )
	{
		return new ReflectingKey<>( this, method, index );
	}

	@Override public Class<? super T> interfaceType()
	{
		return interfaceType;
	}

	@Override public List<MethodKey<T>> keys()
	{
		return List.copyOf( keys );
	}

	@Override public MethodKey<T> keyByMethod( Method method )
	{
		return Kit.map.get( keysByMethod, method );
	}

	@Override public T newEntwiner( Anycall<T> exitPoint )
	{
		return new ReflectingEntwiner<>( this, exitPoint ).entryPoint;
	}

	@Override public Anycall<T> newUntwiner( T exitPoint )
	{
		return new ReflectingUntwiner<>( this, exitPoint ).anycall;
	}

	Optional<ReflectingKey<T>> tryGetKeyByMethod( Method method )
	{
		return Kit.map.getOptional( keysByMethod, method );
	}
}
