package mikenakis.testana.discovery;

import mikenakis.kit.Kit;

import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * Represents a module of the project.
 *
 * @author michael.gr
 */
public abstract class DiscoveryModule
{
	protected DiscoveryModule()
	{
	}

	public abstract String name();

	public abstract void resolveDependencies( Map<String,DiscoveryModule> nameToModuleMap );

	public abstract Path sourcePath();

	public abstract Collection<OutputDirectory> outputDirectories();

	public abstract Collection<DiscoveryModule> projectDependencies();

	public abstract Collection<Path> externalDependencyPaths();

	public abstract Collection<DiscoveryModule> nestedModules();

	@Override public abstract String toString();

	public Collection<Path> allOutputPaths()
	{
		Collection<Path> mutablePaths = new LinkedHashSet<>();
		allOutputPathsRecursive( this, mutablePaths );
		return mutablePaths;
	}

	private static void allOutputPathsRecursive( DiscoveryModule discoveryModule, Collection<Path> mutableOutputPaths )
	{
		for( OutputDirectory outputDirectory : discoveryModule.outputDirectories() )
			Kit.collection.tryAdd( mutableOutputPaths, outputDirectory.path );
		mutableOutputPaths.addAll( discoveryModule.externalDependencyPaths() );
		for( DiscoveryModule dependency : discoveryModule.projectDependencies() )
			allOutputPathsRecursive( dependency, mutableOutputPaths );
	}
}
