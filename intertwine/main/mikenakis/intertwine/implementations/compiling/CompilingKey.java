package mikenakis.intertwine.implementations.compiling;

import mikenakis.bytecode.model.descriptors.MethodPrototype;
import mikenakis.intertwine.Intertwine;
import mikenakis.intertwine.MethodKey;

/**
 * A {@link MethodKey} for the {@link CompilingIntertwine}.
 *
 * NOTE: the class and field 'index' must both be public in order to be accessible by compiled untwiner
 *
 * @author michael.gr
 */
public class CompilingKey<T> implements MethodKey<T>
{
	private final CompilingIntertwine<T> intertwine;
	final MethodPrototype methodPrototype;
	public final int index;

	CompilingKey( CompilingIntertwine<T> intertwine, MethodPrototype methodPrototype, int index )
	{
		this.intertwine = intertwine;
		this.methodPrototype = methodPrototype;
		this.index = index;
	}

	@Override public final Intertwine<T> getIntertwine()
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
