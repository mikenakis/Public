package io.github.mikenakis.intertwine.test.comparisons.implementations.alternative.methodhandle;

import io.github.mikenakis.intertwine.Anycall;
import io.github.mikenakis.intertwine.Intertwine;
import io.github.mikenakis.intertwine.MethodKey;
import io.github.mikenakis.kit.Kit;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Method Handle {@link Intertwine}.
 *
 * @author michael.gr
 */
class MethodHandleIntertwine<T> implements Intertwine<T>
{
	private final Class<? super T> interfaceType;
	final List<MethodHandleKey<T>> keys;
	private final Map<Method,MethodHandleKey<T>> keysByMethod;

	MethodHandleIntertwine( Class<? super T> interfaceType )
	{
		assert interfaceType.isInterface();
		assert Modifier.isPublic( interfaceType.getModifiers() ) : new IllegalAccessException();
		this.interfaceType = interfaceType;
		MethodHandles.Lookup lookup = MethodHandles.publicLookup().in( interfaceType );
		Method[] methods = interfaceType.getMethods();
		keys = IntStream.range( 0, methods.length ).mapToObj( i -> createKey( lookup, methods[i], i ) ).toList();
		keysByMethod = keys.stream().collect( Collectors.toMap( k -> k.method, k -> k ) );
	}

	private MethodHandleKey<T> createKey( MethodHandles.Lookup lookup, Method method, int index )
	{
		MethodHandle methodHandle = Kit.unchecked( () -> lookup.unreflect( method ) );
		return new MethodHandleKey<>( this, method, methodHandle, index );
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
		return new MethodHandleEntwiner<>( this, exitPoint ).entryPoint;
	}

	@Override public Anycall<T> newUntwiner( T exitPoint )
	{
		return new MethodHandleUntwiner<>( this, exitPoint ).anycall;
	}

	Optional<MethodHandleKey<T>> tryGetKeyByMethod( Method method )
	{
		return Kit.map.getOptional( keysByMethod, method );
	}
}
