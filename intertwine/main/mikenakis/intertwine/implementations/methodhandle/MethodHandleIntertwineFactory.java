package mikenakis.intertwine.implementations.methodhandle;

import mikenakis.intertwine.Intertwine;
import mikenakis.intertwine.IntertwineFactory;

/**
 * A {@link IntertwineFactory} for {@link MethodHandleIntertwine}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class MethodHandleIntertwineFactory implements IntertwineFactory
{
	public static IntertwineFactory instance = new MethodHandleIntertwineFactory();

	private MethodHandleIntertwineFactory()
	{
	}

	@Override public <T> Intertwine<T> getIntertwine( Class<? super T> interfaceType )
	{
		return new MethodHandleIntertwine<>( interfaceType );
	}
}
