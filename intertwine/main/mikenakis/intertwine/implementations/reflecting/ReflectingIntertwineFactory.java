package mikenakis.intertwine.implementations.reflecting;

import mikenakis.intertwine.Intertwine;
import mikenakis.intertwine.IntertwineFactory;

/**
 * A {@link IntertwineFactory} for {@link ReflectingIntertwine}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class ReflectingIntertwineFactory implements IntertwineFactory
{
	public static ReflectingIntertwineFactory instance = new ReflectingIntertwineFactory();

	private ReflectingIntertwineFactory()
	{
	}

	@Override public <T> Intertwine<T> getIntertwine( Class<? super T> interfaceType )
	{
		return new ReflectingIntertwine<>( interfaceType );
	}
}
