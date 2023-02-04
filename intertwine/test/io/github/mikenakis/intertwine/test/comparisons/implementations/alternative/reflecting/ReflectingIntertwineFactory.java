package io.github.mikenakis.intertwine.test.comparisons.implementations.alternative.reflecting;

import io.github.mikenakis.intertwine.Intertwine;
import io.github.mikenakis.intertwine.IntertwineFactory;
import io.github.mikenakis.intertwine.test.comparisons.rig.FooInterface;

import java.lang.reflect.Modifier;

/**
 * A {@link IntertwineFactory} for {@link ReflectingIntertwine}.
 *
 * @author michael.gr
 */
public class ReflectingIntertwineFactory implements IntertwineFactory
{
	public static ReflectingIntertwineFactory instance = new ReflectingIntertwineFactory();

	private ReflectingIntertwineFactory()
	{
	}

	@Override public <T> Intertwine<T> getIntertwine( Class<T> interfaceType, boolean implementDefaultMethods )
	{
		assert Modifier.isPublic( interfaceType.getModifiers() ) : new IllegalAccessException();
		assert interfaceType == FooInterface.class;
		assert !implementDefaultMethods;
		return new ReflectingIntertwine<>( interfaceType );
	}
}
