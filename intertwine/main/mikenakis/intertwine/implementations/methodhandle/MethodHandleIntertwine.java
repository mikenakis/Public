package mikenakis.intertwine.implementations.methodhandle;

import mikenakis.bytecode.model.descriptors.MethodPrototype;
import mikenakis.intertwine.AnyCall;
import mikenakis.intertwine.Intertwine;
import mikenakis.intertwine.MethodKey;
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
import java.util.stream.IntStream;

/**
 * Method Handle {@link Intertwine}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
class MethodHandleIntertwine<T> implements Intertwine<T>
{
	private final Class<? super T> interfaceType;
	final List<MethodHandleKey<T>> keys;
	private final Map<MethodPrototype,MethodHandleKey<T>> keysByPrototype;
	private final Map<Method,MethodHandleKey<T>> keysByMethod;

	MethodHandleIntertwine( Class<? super T> interfaceType )
	{
		assert interfaceType.isInterface();
		assert Modifier.isPublic( interfaceType.getModifiers() ) : new IllegalAccessException();
		this.interfaceType = interfaceType;
		MethodHandles.Lookup lookup = MethodHandles.publicLookup().in( interfaceType );
		Method[] methods = interfaceType.getMethods();
		keys = IntStream.range( 0, methods.length ).mapToObj( i -> createKey( lookup, methods[i], i ) ).collect( Collectors.toList());
		keysByPrototype = keys.stream().collect( Collectors.toMap( k -> k.methodPrototype, k -> k ) );
		keysByMethod = keys.stream().collect( Collectors.toMap( k -> k.method, k -> k ) );
	}

	private MethodHandleKey<T> createKey( MethodHandles.Lookup lookup, Method method, int index )
	{
		MethodPrototype methodPrototype = MethodPrototype.of( method );
		MethodHandle methodHandle = Kit.unchecked( () -> lookup.unreflect( method ) );
		return new MethodHandleKey<>( this, method, methodPrototype, methodHandle, index );
	}

	@Override public Class<? super T> interfaceType()
	{
		return interfaceType;
	}

	@Override public Collection<MethodKey<T>> keys()
	{
		return Kit.collection.downCast( keys );
	}

	@Override public MethodKey<T> keyByIndex( int index )
	{
		return keys.get( index );
	}

	@Override public MethodKey<T> keyByMethodPrototype( MethodPrototype methodPrototype )
	{
		return Kit.map.get( keysByPrototype, methodPrototype );
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
