package mikenakis.test.intertwine.implementations.testing.handwritten_compiled;

import mikenakis.bytecode.model.descriptors.MethodPrototype;
import mikenakis.intertwine.Intertwine;
import mikenakis.intertwine.MethodKey;
import mikenakis.test.intertwine.rig.FooInterface;

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

	@Deprecated @Override public final Intertwine<FooInterface> getIntertwine() { return intertwine; }
	@Deprecated @Override public int methodIndex() { return methodIndex; }
	@Deprecated @Override public MethodPrototype methodPrototype() { return methodPrototype; }
	@Override public String toString() { return "[" + methodIndex + "]: " + methodPrototype.asString(); }
}
