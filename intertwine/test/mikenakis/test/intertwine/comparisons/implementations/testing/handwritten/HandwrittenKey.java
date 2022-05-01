package mikenakis.test.intertwine.comparisons.implementations.testing.handwritten;

import mikenakis.bytecode.model.descriptors.MethodPrototype;
import mikenakis.intertwine.Intertwine;
import mikenakis.intertwine.MethodKey;
import mikenakis.test.intertwine.comparisons.rig.FooInterface;

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
