package io.github.mikenakis.intertwine.test.comparisons.implementations.alternative.methodhandle;

import io.github.mikenakis.intertwine.Intertwine;
import io.github.mikenakis.intertwine.MethodKey;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * A {@link MethodKey} for the {@link MethodHandleIntertwine}.
 *
 * @author michael.gr
 */
class MethodHandleKey<T> implements MethodKey<T>
{
	private final MethodHandleIntertwine<T> intertwine;
	final Method method;
	final MethodHandle methodHandle;
	final int index;

	MethodHandleKey( MethodHandleIntertwine<T> intertwine, Method method, MethodHandle methodHandle, int index )
	{
		this.intertwine = intertwine;
		this.method = method;
		this.methodHandle = methodHandle;
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

	@Override public Method method()
	{
		return method;
	}

	@Override public String toString()
	{
		return method.toString();
	}

	@Deprecated @Override public boolean equals( Object o )
	{
		return o instanceof MethodHandleKey<?> kin && equals( kin );
	}

	public boolean equals( MethodHandleKey<?> that )
	{
		assert intertwine == that.intertwine; //this probably means that you forgot to make use of the caching intertwine factory.
		return method.equals( that.method ) && methodHandle.equals( that.methodHandle );
	}

	@Override public int hashCode()
	{
		//NOTE: we intentionally refrain from including the methodHandle in the hash, so that two keys that only differ by methodHandle will have
		//      the same hashCode, so equals() will eventually be called to check whether they are equal.  (See the equals() method.)
		return Objects.hash( method /*, methodHandle*/ );
	}
}
