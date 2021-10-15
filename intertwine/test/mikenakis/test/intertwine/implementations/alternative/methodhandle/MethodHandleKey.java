package mikenakis.test.intertwine.implementations.alternative.methodhandle;

import mikenakis.bytecode.model.descriptors.MethodPrototype;
import mikenakis.intertwine.Intertwine;
import mikenakis.intertwine.MethodKey;

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
	final MethodPrototype methodPrototype;
	final MethodHandle methodHandle;
	final int index;

	MethodHandleKey( MethodHandleIntertwine<T> intertwine, Method method, MethodPrototype methodPrototype, MethodHandle methodHandle, int index )
	{
		this.intertwine = intertwine;
		this.method = method;
		this.methodPrototype = methodPrototype;
		this.methodHandle = methodHandle;
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

	@Override public String toString()
	{
		return methodPrototype.asString();
	}

	@Override public boolean equals( Object o )
	{
		if( this == o ) return true;
		if( o == null || getClass() != o.getClass() ) return false;
		return equals( (MethodHandleKey<?>)o );
	}

	public boolean equals( MethodHandleKey<?> that )
	{
		assert intertwine == that.intertwine; //this probably means that you forgot to make use of the caching intertwine factory.
		return method.equals( that.method ) &&
			methodPrototype.equals( that.methodPrototype ) &&
			methodHandle.equals( that.methodHandle );
	}

	@Override public int hashCode()
	{
		//NOTE: we intentionally refrain from including the methodHandle in the hash, so that two keys that only differ by methodHandle will have
		//      the same hashCode, so equals() will eventually be called to check whether they are equal.  (See the equals() method.)
		return Objects.hash( method, methodPrototype/*, methodHandle*/ );
	}
}
