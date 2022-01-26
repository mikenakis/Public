package mikenakis.test.intertwine.comparisons.implementations.testing.handwritten;

import mikenakis.intertwine.Intertwine;
import mikenakis.intertwine.IntertwineFactory;
import mikenakis.test.intertwine.comparisons.rig.FooInterface;

import java.lang.reflect.Modifier;

/**
 * A {@link IntertwineFactory} for {@link HandwrittenIntertwine}.
 *
 * @author michael.gr
 */
public class HandwrittenIntertwineFactory implements IntertwineFactory
{
	public static HandwrittenIntertwineFactory instance = new HandwrittenIntertwineFactory();

	private HandwrittenIntertwineFactory()
	{
	}

	@Override public <T> Intertwine<T> getIntertwine( Class<? super T> interfaceType )
	{
		assert Modifier.isPublic( interfaceType.getModifiers() ) : new IllegalAccessException();
		assert interfaceType == FooInterface.class;
		@SuppressWarnings( "unchecked" ) Intertwine<T> result = (Intertwine<T>)new HandwrittenIntertwine();
		return result;
	}
}
