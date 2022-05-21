package io.github.mikenakis.testana.structure.cache;

import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.testana.discovery.OutputDirectory;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

class CacheModule
{
	final String name;
	private final Map<Path,CacheOutputDirectory> cacheOutputDirectories;

	CacheModule( String name, Map<Path,CacheOutputDirectory> cacheOutputDirectories )
	{
		this.name = name;
		this.cacheOutputDirectories = cacheOutputDirectories;
	}

	Optional<CacheType> tryGetType( OutputDirectory outputDirectory, String className )
	{
		Optional<CacheOutputDirectory> cacheOutputDirectory = Optional.ofNullable( Kit.map.tryGet( cacheOutputDirectories, outputDirectory.path ) );
		return cacheOutputDirectory.flatMap( o -> o.tryGetType( className ) );
	}

	Iterable<CacheOutputDirectory> outputDirectories()
	{
		return cacheOutputDirectories.values();
	}

	int typeCount()
	{
		return cacheOutputDirectories.values().stream().map( o -> o.size() ).reduce( 0, ( a, b ) -> a + b );
	}
}
