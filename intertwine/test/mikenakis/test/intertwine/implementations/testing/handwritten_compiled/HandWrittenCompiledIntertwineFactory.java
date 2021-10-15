package mikenakis.test.intertwine.implementations.testing.handwritten_compiled;

import mikenakis.intertwine.Intertwine;
import mikenakis.intertwine.IntertwineFactory;
import mikenakis.test.intertwine.rig.FooInterface;

import java.lang.reflect.Modifier;

/**
 * A {@link IntertwineFactory} for {@link HandWrittenCompiledIntertwine}.
 *
 * @author michael.gr
 */
public class HandWrittenCompiledIntertwineFactory implements IntertwineFactory
{
	public static HandWrittenCompiledIntertwineFactory instance = new HandWrittenCompiledIntertwineFactory();

	private HandWrittenCompiledIntertwineFactory()
	{
	}

	@Override public <T> Intertwine<T> getIntertwine( Class<? super T> interfaceType )
	{
		assert Modifier.isPublic( interfaceType.getModifiers() ) : new IllegalAccessException();
		assert interfaceType == FooInterface.class;
		@SuppressWarnings( "unchecked" ) Intertwine<T> result = (Intertwine<T>)new HandWrittenCompiledIntertwine( this );
		return result;
	}
}
