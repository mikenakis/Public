package mikenakis.intertwine.implementations.reflecting2;

import mikenakis.intertwine.Intertwine;

import java.lang.reflect.Method;

/**
 * A {@link Intertwine.Key} for the {@link Reflecting2Intertwine}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
class Reflecting2Key<T> implements Intertwine.Key<T>
{
	private final Reflecting2Intertwine<T> intertwine;
	final Method method;
	final String signatureString;

	Reflecting2Key( Reflecting2Intertwine<T> intertwine, Method method, String signatureString )
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
