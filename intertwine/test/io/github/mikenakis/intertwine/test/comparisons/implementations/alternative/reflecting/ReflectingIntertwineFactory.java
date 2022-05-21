package io.github.mikenakis.intertwine.test.comparisons.implementations.alternative.reflecting;

import io.github.mikenakis.intertwine.Intertwine;
import io.github.mikenakis.intertwine.IntertwineFactory;

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

	@Override public <T> Intertwine<T> getIntertwine( Class<? super T> interfaceType )
	{
		return new ReflectingIntertwine<>( interfaceType );
	}
}
