package io.github.mikenakis.intertwine.predefined;

import io.github.mikenakis.bytecode.model.descriptors.MethodPrototype;
import io.github.mikenakis.intertwine.Intertwine;
import io.github.mikenakis.kit.UncheckedException;

/**
 * 'Method Not Found' {@link UncheckedException}.
 *
 * @author michael.gr
 */
public class MethodNotFoundException extends UncheckedException
{
	public final Intertwine<?> intertwine;
	public final MethodPrototype methodPrototype;

	public MethodNotFoundException( Intertwine<?> intertwine, MethodPrototype methodPrototype )
	{
		this.intertwine = intertwine;
		this.methodPrototype = methodPrototype;
	}
}
