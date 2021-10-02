package mikenakis.test.intertwine.handwritten;

import mikenakis.intertwine.Intertwine;
import mikenakis.intertwine.IntertwineFactory;
import mikenakis.test.intertwine.rig.FooInterface;

import java.lang.reflect.Modifier;

/**
 * A {@link IntertwineFactory} for {@link HandwrittenIntertwine}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class HandwrittenIntertwineFactory implements IntertwineFactory
{
	public static HandwrittenIntertwineFactory instance = new HandwrittenIntertwineFactory();

	private HandwrittenIntertwineFactory()
	{
	}

	@Override public <T> Intertwine<T> getIntertwine( Class<? super T> interfaceType )
	{
		if( !Modifier.isPublic( interfaceType.getModifiers() ) )
			throw new RuntimeException( new IllegalAccessException() );
		assert interfaceType == FooInterface.class;
		@SuppressWarnings( "unchecked" ) Intertwine<T> result = (Intertwine<T>)new HandwrittenIntertwine();
		return result;
	}
}
