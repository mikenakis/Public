package mikenakis.intertwine.implementations.reflecting;

import mikenakis.bytecode.model.descriptors.MethodPrototype;
import mikenakis.intertwine.Intertwine;

import java.lang.reflect.Method;

/**
 * A {@link Intertwine.Key} for the {@link ReflectingIntertwine}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
class ReflectingKey<T> implements Intertwine.Key<T>
{
	private final ReflectingIntertwine<T> intertwine;
	final Method method;
	final MethodPrototype methodPrototype;
	final int index;

	ReflectingKey( ReflectingIntertwine<T> intertwine, Method method, MethodPrototype methodPrototype, int index )
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
