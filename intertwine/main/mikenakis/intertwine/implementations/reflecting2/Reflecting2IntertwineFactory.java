package mikenakis.intertwine.implementations.reflecting2;

import mikenakis.intertwine.Intertwine;
import mikenakis.intertwine.IntertwineFactory;

/**
 * A {@link IntertwineFactory} for {@link Reflecting2Intertwine}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class Reflecting2IntertwineFactory implements IntertwineFactory
{
	public static Reflecting2IntertwineFactory instance = new Reflecting2IntertwineFactory();

	private Reflecting2IntertwineFactory()
	{
	}

	@Override public <T> Intertwine<T> getIntertwine( Class<? super T> interfaceType )
	{
		return new Reflecting2Intertwine<>( interfaceType );
	}
}
