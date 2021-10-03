package mikenakis.test.intertwine.handwritten_compiled;

import mikenakis.bytecode.ByteCodeClassLoader;
import mikenakis.intertwine.Intertwine;
import mikenakis.intertwine.IntertwineFactory;
import mikenakis.test.intertwine.rig.FooInterface;

import java.lang.reflect.Modifier;

/**
 * A {@link IntertwineFactory} for {@link HandWrittenCompiledIntertwine}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class HandWrittenCompiledIntertwineFactory implements IntertwineFactory
{
	public static HandWrittenCompiledIntertwineFactory instance = new HandWrittenCompiledIntertwineFactory();

	ByteCodeClassLoader byteCodeClassLoader = new ByteCodeClassLoader();

	private HandWrittenCompiledIntertwineFactory()
	{
	}

	@Override public <T> Intertwine<T> getIntertwine( Class<? super T> interfaceType )
	{
		if( !Modifier.isPublic( interfaceType.getModifiers() ) )
			throw new RuntimeException( new IllegalAccessException() );
		assert interfaceType == FooInterface.class;
		@SuppressWarnings( "unchecked" ) Intertwine<T> result = (Intertwine<T>)new HandWrittenCompiledIntertwine( this );
		return result;
	}
}
