package io.github.mikenakis.intertwine.test.comparisons.implementations.testing.handwritten;

import io.github.mikenakis.intertwine.Intertwine;
import io.github.mikenakis.intertwine.MethodKey;
import io.github.mikenakis.intertwine.test.comparisons.rig.FooInterface;

import java.lang.reflect.Method;

/**
 * A {@link MethodKey} for the {@link HandwrittenIntertwine}.
 *
 * @author michael.gr
 */
class HandwrittenKey implements MethodKey<FooInterface>
{
	private final HandwrittenIntertwine intertwine;
	final int index;
	final Method method;

	HandwrittenKey( HandwrittenIntertwine intertwine, int index, Method method )
	{
		this.intertwine = intertwine;
		this.index = index;
		this.method = method;
	}

	@Override public final Intertwine<FooInterface> intertwine()
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
