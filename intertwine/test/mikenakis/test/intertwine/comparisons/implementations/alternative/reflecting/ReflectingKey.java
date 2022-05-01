package mikenakis.test.intertwine.comparisons.implementations.alternative.reflecting;

import mikenakis.bytecode.model.descriptors.MethodPrototype;
import mikenakis.intertwine.Intertwine;
import mikenakis.intertwine.MethodKey;

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
	final MethodPrototype methodPrototype;
	final int index;

	ReflectingKey( ReflectingIntertwine<T> intertwine, Method method, MethodPrototype methodPrototype, int index )
	{
		this.intertwine = intertwine;
		this.method = method;
		this.methodPrototype = methodPrototype;
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

	@Override public MethodPrototype methodPrototype()
	{
		return methodPrototype;
	}
}
