package io.github.mikenakis.testana.structure.cache;

import io.github.mikenakis.kit.Kit;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

class CacheOutputDirectory
{
	final Path path;
	private final Map<String,CacheType> types;

	CacheOutputDirectory( Path path, Map<String,CacheType> types )
	{
		this.path = path;
		this.types = types;
	}

	Optional<CacheType> tryGetType( String className )
	{
		return Optional.ofNullable( Kit.map.tryGet( types, className ) );
	}

	Iterable<CacheType> types()
	{
		return types.values();
	}

	int size()
	{
		return types.size();
	}
}
