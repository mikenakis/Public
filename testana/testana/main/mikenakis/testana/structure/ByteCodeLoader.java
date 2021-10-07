package mikenakis.testana.structure;

import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.kit.Kit;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * ByteCode loader.
 *
 * @author Michael Belivanakis (michael.gr)
 */
final class ByteCodeLoader
{
	private final ClassLoader classLoader;

	ByteCodeLoader( ClassLoader classLoader )
	{
		this.classLoader = classLoader;
	}

	ByteCodeType getByteCodeTypeByName( String className )
	{
		Path path = Kit.classLoading.getPathFromClassLoaderAndTypeName( classLoader, className );
		byte[] bytes = Kit.unchecked( () -> Files.readAllBytes( path ) );
		return ByteCodeType.read( bytes );
	}
}
