package io.github.mikenakis.intertwine.test.comparisons.implementations.alternative.methodhandle;

import io.github.mikenakis.intertwine.Intertwine;
import io.github.mikenakis.intertwine.IntertwineFactory;
import io.github.mikenakis.intertwine.test.comparisons.rig.FooInterface;

import java.lang.reflect.Modifier;

/**
 * A {@link IntertwineFactory} for {@link MethodHandleIntertwine}.
 *
 * @author michael.gr
 */
public final class MethodHandleIntertwineFactory implements IntertwineFactory
{
	public static IntertwineFactory instance = new MethodHandleIntertwineFactory();

	private MethodHandleIntertwineFactory()
	{
	}

	@Override public <T> Intertwine<T> getIntertwine( Class<T> interfaceType, boolean implementDefaultMethods )
	{
		assert Modifier.isPublic( interfaceType.getModifiers() ) : new IllegalAccessException();
		assert interfaceType == FooInterface.class;
		assert !implementDefaultMethods;
		return new MethodHandleIntertwine<>( interfaceType );
	}
}
