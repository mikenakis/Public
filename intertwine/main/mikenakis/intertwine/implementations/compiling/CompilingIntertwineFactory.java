package mikenakis.intertwine.implementations.compiling;

import mikenakis.bytecode.ByteCodeClassLoader;
import mikenakis.intertwine.Intertwine;
import mikenakis.intertwine.IntertwineFactory;

/**
 * A {@link IntertwineFactory} for {@link CompilingIntertwine}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class CompilingIntertwineFactory implements IntertwineFactory
{
	public static CompilingIntertwineFactory instance = new CompilingIntertwineFactory( getClassLoader() );

	ByteCodeClassLoader byteCodeClassLoader;

	private CompilingIntertwineFactory( ClassLoader parentClassLoader )
	{
		byteCodeClassLoader = new ByteCodeClassLoader( parentClassLoader );
	}

	@Override public <T> Intertwine<T> getIntertwine( Class<? super T> interfaceType )
	{
		return new CompilingIntertwine<>( this, interfaceType );
	}

	private static ClassLoader getClassLoader()
	{
		//return Thread.currentThread().getContextClassLoader(); does not work
		return CompilingIntertwineFactory.class.getClassLoader(); // works
	}
}
