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
	public static HandWrittenCompiledIntertwineFactory instance = new HandWrittenCompiledIntertwineFactory( getClassLoader() );

	ByteCodeClassLoader byteCodeClassLoader;

	private HandWrittenCompiledIntertwineFactory( ClassLoader parentClassLoader )
	{
		//Kit.classLoading.getContents( parentClassLoader ).forEach( s -> Log.debug( s ) );
		byteCodeClassLoader = new ByteCodeClassLoader( parentClassLoader );
	}

	@Override public <T> Intertwine<T> getIntertwine( Class<? super T> interfaceType )
	{
		if( !Modifier.isPublic( interfaceType.getModifiers() ) )
			throw new RuntimeException( new IllegalAccessException() );
		assert interfaceType == FooInterface.class;
		@SuppressWarnings( "unchecked" ) Intertwine<T> result = (Intertwine<T>)new HandWrittenCompiledIntertwine( this );
		return result;
	}

	private static ClassLoader getClassLoader()
	{
		//return Thread.currentThread().getContextClassLoader(); does not work
		return HandWrittenCompiledIntertwineFactory.class.getClassLoader(); // works
	}
}
