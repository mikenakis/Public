package mikenakis.test.intertwine.rig.exchange.object;

import mikenakis.bytecode.model.descriptors.MethodPrototype;

public final class AnycallRequest
{
	public final MethodPrototype methodPrototype;
	public final Object[] arguments;

	public AnycallRequest( MethodPrototype methodPrototype, Object[] arguments )
	{
		this.methodPrototype = methodPrototype;
		this.arguments = arguments;
	}

	@Override public String toString()
	{
		return "signatureString:" + methodPrototype;
	}
}
