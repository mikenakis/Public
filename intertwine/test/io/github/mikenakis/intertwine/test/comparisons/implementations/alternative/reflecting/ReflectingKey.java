package io.github.mikenakis.intertwine.test.comparisons.implementations.alternative.reflecting;

import io.github.mikenakis.intertwine.Intertwine;
import io.github.mikenakis.intertwine.MethodKey;

import java.lang.reflect.Method;

/**
 * A {@link MethodKey} for the {@link ReflectingIntertwine}.
 *
 * @author michael.gr
 */
class ReflectingKey<T> implements MethodKey<T>
{
	private final ReflectingIntertwine<T> intertwine;
	final Method method;
	final int index;

	ReflectingKey( ReflectingIntertwine<T> intertwine, Method method, int index )
	{
		this.intertwine = intertwine;
		this.method = method;
		this.index = index;
	}

	@Override public final Intertwine<T> intertwine()
	{
		return intertwine;
	}

	@Override public int methodIndex()
	{
		return index;
	}

	@Override public Method method()
	{
		return method;
	}
}
