package io.github.mikenakis.intertwine.test.comparisons.implementations.testing.handwritten;

import io.github.mikenakis.bytecode.model.descriptors.MethodPrototype;
import io.github.mikenakis.intertwine.Intertwine;
import io.github.mikenakis.intertwine.MethodKey;
import io.github.mikenakis.intertwine.test.comparisons.rig.FooInterface;

/**
 * A {@link MethodKey} for the {@link HandwrittenIntertwine}.
 *
 * @author michael.gr
 */
class HandwrittenKey implements MethodKey<FooInterface>
{
	private final HandwrittenIntertwine intertwine;
	final int index;
	final MethodPrototype methodPrototype;

	HandwrittenKey( HandwrittenIntertwine intertwine, int index, MethodPrototype methodPrototype )
	{
		this.intertwine = intertwine;
		this.index = index;
		this.methodPrototype = methodPrototype;
	}

	@Override public final Intertwine<FooInterface> intertwine()
	{
		return intertwine;
	}

	@Override public int methodIndex()
	{
		return index;
	}

	@Override public MethodPrototype methodPrototype()
	{
		return methodPrototype;
	}
}
