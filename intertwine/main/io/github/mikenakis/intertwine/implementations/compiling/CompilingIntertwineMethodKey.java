package io.github.mikenakis.intertwine.implementations.compiling;

import io.github.mikenakis.intertwine.Intertwine;
import io.github.mikenakis.intertwine.MethodKey;

import java.lang.reflect.Method;

/**
 * A {@link MethodKey} for the {@link CompilingIntertwine}.
 * <p>
 * NOTE: both this class and its field 'index' must be public in order to be accessible by compiled untwiners.
 *
 * @author michael.gr
 */
public class CompilingIntertwineMethodKey<T> implements MethodKey<T>
{
	private final CompilingIntertwine<T> intertwine;
	final Method method;
	public final int index;

	CompilingIntertwineMethodKey( CompilingIntertwine<T> intertwine, Method method, int index )
	{
		this.intertwine = intertwine;
		this.method = method;
		this.index = index;
	}

	@Override public final Intertwine<T> intertwine() { return intertwine; }
	@Override public int methodIndex() { return index; }
	@Override public Method method() { return method; }
	@Override public String toString() { return "#" + index + ": " + method; }
}
