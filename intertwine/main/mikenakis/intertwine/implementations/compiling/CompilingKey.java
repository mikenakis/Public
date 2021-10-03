package mikenakis.intertwine.implementations.compiling;

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
	final String prototypeString;
	final int index;

	CompilingKey( CompilingIntertwine<T> intertwine, Method method, String prototypeString, int index )
	{
		this.intertwine = intertwine;
		this.method = method;
		this.prototypeString = prototypeString;
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
}
