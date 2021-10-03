package mikenakis.intertwine.implementations.methodhandle;

import mikenakis.intertwine.Intertwine;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * A {@link Intertwine.Key} for the {@link MethodHandleIntertwine}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
class MethodHandleKey<T> implements Intertwine.Key<T>
{
	private final MethodHandleIntertwine<T> intertwine;
	final Method method;
	final String prototypeString;
	final MethodHandle methodHandle;
	final int index;

	MethodHandleKey( MethodHandleIntertwine<T> intertwine, Method method, String prototypeString, MethodHandle methodHandle, int index )
	{
		this.intertwine = intertwine;
		this.method = method;
		this.prototypeString = prototypeString;
		this.methodHandle = methodHandle;
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

	@Override public String getPrototypeString()
	{
		return prototypeString;
	}

	@Override public String toString()
	{
		return prototypeString;
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
			prototypeString.equals( that.prototypeString ) &&
			methodHandle.equals( that.methodHandle );
	}

	@Override public int hashCode()
	{
		//NOTE: we intentionally refrain from including the methodHandle in the hash, so that two keys that only differ by methodHandle will have
		//      the same hashCode, so equals() will eventually be called to check whether they are equal.  (See the equals() method.)
		return Objects.hash( method, prototypeString/*, methodHandle*/ );
	}
}
