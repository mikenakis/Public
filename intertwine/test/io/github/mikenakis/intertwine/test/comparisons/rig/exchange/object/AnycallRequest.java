package io.github.mikenakis.intertwine.test.comparisons.rig.exchange.object;

import java.lang.reflect.Method;

public final class AnycallRequest
{
	public final Method method;
	public final Object[] arguments;

	public AnycallRequest( Method method, Object[] arguments )
	{
		this.method = method;
		this.arguments = arguments;
	}

	@Override public String toString()
	{
		return "signatureString:" + method;
	}
}
