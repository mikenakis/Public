package mikenakis.testana.structure;

import mikenakis.bytecode.ByteCodeType;
import mikenakis.kit.Kit;
import mikenakis.kit.logging.Log;
import mikenakis.services.bytecode.ByteCodeService;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;

/**
 * ByteCode loader.
 *
 * @author Michael Belivanakis (michael.gr)
 */
final class ClassAndByteCodeLoader
{
	private final ClassLoader interceptingClassLoader;

	ClassAndByteCodeLoader( Collection<Path> classPath )
	{
		interceptingClassLoader = ByteCodeService.instance.newInterceptingByteCodeClassLoader( classPath );
	}

	Optional<ByteCodeType> tryGetByteCodeTypeByName( String className )
	{
		return ByteCodeService.instance.tryGetByteCodeTypeByName( className, interceptingClassLoader );
	}

	Class<?> getClassByName( String typeName )
	{
		return Kit.unchecked( () -> interceptingClassLoader.loadClass( typeName ) );
	}

	Optional<Class<?>> tryGetClassByName( String typeName )
	{
		try
		{
			return Optional.of( getClassByName( typeName ) );
		}
		catch( Throwable e )
		{
			Log.warning( "could not load class '" + typeName + "': " + e.getClass() + ": " + e.getMessage() );
			return Optional.empty();
		}
	}
}
