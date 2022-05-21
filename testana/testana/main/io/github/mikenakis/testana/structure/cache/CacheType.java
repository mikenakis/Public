package io.github.mikenakis.testana.structure.cache;

import java.util.Collection;
import java.util.Optional;

public class CacheType
{
	final String className;
	public final Collection<String> dependencyNames;
	public final Optional<String> testEngineName;

	CacheType( String className, Collection<String> dependencyNames, Optional<String> testEngineName )
	{
		this.className = className;
		this.dependencyNames = dependencyNames;
		this.testEngineName = testEngineName;
	}
}
