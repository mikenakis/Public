package mikenakis.test.intertwine.handwritten;

import mikenakis.bytecode.model.descriptors.MethodPrototype;
import mikenakis.intertwine.Intertwine;
import mikenakis.test.intertwine.rig.FooInterface;

/**
 * A {@link Intertwine.Key} for the {@link HandwrittenIntertwine}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
class HandwrittenKey implements Intertwine.Key<FooInterface>
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

	@Override public final Intertwine<FooInterface> getIntertwine()
	{
		return intertwine;
	}

	@Override public int getIndex()
	{
		return index;
	}

	@Override public MethodPrototype getMethodPrototype()
	{
		return methodPrototype;
	}
}
