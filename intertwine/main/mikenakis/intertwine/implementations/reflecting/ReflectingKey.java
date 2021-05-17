package mikenakis.intertwine.implementations.reflecting;

import mikenakis.intertwine.Intertwine;

import java.lang.reflect.Method;

/**
 * A {@link Intertwine.Key} for the {@link ReflectingIntertwine}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
class ReflectingKey<T> implements Intertwine.Key<T>
{
	private final ReflectingIntertwine<T> intertwine;
	final Method method;
	final String signatureString;

	ReflectingKey( ReflectingIntertwine<T> intertwine, Method method, String signatureString )
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
