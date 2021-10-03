package mikenakis.intertwine.implementations.compiling;

import mikenakis.bytecode.model.descriptors.MethodPrototype;
import mikenakis.intertwine.Intertwine;

import java.lang.reflect.Method;

/**
 * A {@link Intertwine.Key} for the {@link CompilingIntertwine}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
class CompilingKey<T> implements Intertwine.Key<T>
{
	private final CompilingIntertwine<T> intertwine;
	final Method method;
	final MethodPrototype methodPrototype;
	final int index;

	CompilingKey( CompilingIntertwine<T> intertwine, Method method, MethodPrototype methodPrototype, int index )
	{
		this.intertwine = intertwine;
		this.method = method;
		this.methodPrototype = methodPrototype;
		this.index = index;
	}

	@Override public final Intertwine<T> getIntertwine()
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
