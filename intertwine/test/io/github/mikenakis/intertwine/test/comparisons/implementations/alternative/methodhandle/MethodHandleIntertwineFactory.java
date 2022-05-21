package io.github.mikenakis.intertwine.test.comparisons.implementations.alternative.methodhandle;

import io.github.mikenakis.intertwine.Intertwine;
import io.github.mikenakis.intertwine.IntertwineFactory;

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

	@Override public <T> Intertwine<T> getIntertwine( Class<? super T> interfaceType )
	{
		return new MethodHandleIntertwine<>( interfaceType );
	}
}
