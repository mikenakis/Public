package io.github.mikenakis.intertwine.test.comparisons.implementations.testing.handwritten_compiled;

import io.github.mikenakis.bytecode.model.descriptors.MethodPrototype;
import io.github.mikenakis.intertwine.Intertwine;
import io.github.mikenakis.intertwine.MethodKey;
import io.github.mikenakis.intertwine.test.comparisons.rig.FooInterface;

import java.lang.reflect.Method;

/**
 * A {@link MethodKey} for the {@link HandWrittenCompiledIntertwine}.
 *
 * Note: this class must be public because it needs to be accessible by the generated untwiner which is loaded by a different classloader.
 *
 * @author michael.gr
 */
public class HandWrittenCompiledKey implements MethodKey<FooInterface>
{
	private final HandWrittenCompiledIntertwine intertwine;
	final Method method;
	final MethodPrototype methodPrototype;
	public final int methodIndex; // must be public because it needs to be accessible by the generated untwiner which is loaded by a different classloader.

	HandWrittenCompiledKey( HandWrittenCompiledIntertwine intertwine, Method method, MethodPrototype methodPrototype, int methodIndex )
	{
		this.intertwine = intertwine;
		this.method = method;
		this.methodPrototype = methodPrototype;
		this.methodIndex = methodIndex;
	}

	@Deprecated @Override public final Intertwine<FooInterface> intertwine() { return intertwine; }
	@Deprecated @Override public int methodIndex() { return methodIndex; }
	@Deprecated @Override public MethodPrototype methodPrototype() { return methodPrototype; }
	@Override public String toString() { return "[" + methodIndex + "]: " + methodPrototype.asString(); }
}
