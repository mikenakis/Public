package mikenakis.intertwine.predefined;

import mikenakis.bytecode.model.descriptors.MethodPrototype;
import mikenakis.intertwine.Intertwine;
import mikenakis.kit.UncheckedException;

/**
 * 'Method Not Found' {@link UncheckedException}.
 *
 * @author Michael Belivanakis (michael.gr)
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
