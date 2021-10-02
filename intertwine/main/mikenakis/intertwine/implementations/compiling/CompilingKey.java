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
	final String signatureString;

	CompilingKey( CompilingIntertwine<T> intertwine, Method method, String signatureString )
	{
		this.intertwine = intertwine;
		this.method = method;
		this.signatureString = signatureString;
	}

	@Override public final Intertwine<T> getIntertwine()
	{
		return intertwine;
	}

	@Override public String getSignatureString()
	{
		return signatureString;
	}
}
