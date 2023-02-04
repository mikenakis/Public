package io.github.mikenakis.intertwine.test.comparisons.implementations.testing.handwritten_compiled;

import io.github.mikenakis.intertwine.Intertwine;
import io.github.mikenakis.intertwine.IntertwineFactory;
import io.github.mikenakis.intertwine.test.comparisons.rig.FooInterface;

import java.lang.reflect.Modifier;

/**
 * A {@link IntertwineFactory} for {@link HandWrittenCompiledIntertwine}.
 *
 * @author michael.gr
 */
public class HandWrittenCompiledIntertwineFactory implements IntertwineFactory
{
	public static HandWrittenCompiledIntertwineFactory instance = new HandWrittenCompiledIntertwineFactory();

	private HandWrittenCompiledIntertwineFactory()
	{
	}

	@Override public <T> Intertwine<T> getIntertwine( Class<T> interfaceType, boolean implementDefaultMethods )
	{
		assert Modifier.isPublic( interfaceType.getModifiers() ) : new IllegalAccessException();
		assert interfaceType == FooInterface.class;
		assert !implementDefaultMethods;
		@SuppressWarnings( "unchecked" ) Intertwine<T> result = (Intertwine<T>)new HandWrittenCompiledIntertwine();
		return result;
	}
}
