package mikenakis.test.intertwine.handwritten;

import mikenakis.intertwine.Intertwine;
import mikenakis.test.intertwine.rig.FooInterface;

import java.lang.reflect.Method;

/**
 * A {@link Intertwine.Key} for the {@link HandwrittenIntertwine}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
class HandwrittenKey implements Intertwine.Key<FooInterface>
{
	private final HandwrittenIntertwine intertwine;
	final int index;
	final Method method;
	final String signatureString;

	HandwrittenKey( HandwrittenIntertwine intertwine, int index, Method method, String signatureString )
	{
		this.intertwine = intertwine;
		this.index = index;
		this.method = method;
		this.signatureString = signatureString;
	}

	@Override public final Intertwine<FooInterface> getIntertwine()
	{
		return intertwine;
	}

	@Override public String getSignatureString()
	{
		return signatureString;
	}
}
