package io.github.mikenakis.testana.structure;

import io.github.mikenakis.bytecode.model.ByteCodeType;
import io.github.mikenakis.kit.Kit;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * ByteCode loader.
 *
 * @author michael.gr
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
