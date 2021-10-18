package mikenakis.testana.discovery;

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
		mutableOutputPaths.addAll( discoveryModule.outputPaths() );
		mutableOutputPaths.addAll( discoveryModule.externalDependencyPaths() );
		for( DiscoveryModule dependency : discoveryModule.projectDependencies() )
			allOutputPathsRecursive( dependency, mutableOutputPaths );
	}

	public Collection<Path> outputPaths()
	{
		return outputDirectories().stream().map( o -> o.path ).toList();
	}

	public Collection<Path> allDependencyAndExternalPaths()
	{
		Collection<Path> mutablePaths = new LinkedHashSet<>();
		mutablePaths.addAll( externalDependencyPaths() );
		for( DiscoveryModule dependency : projectDependencies() )
			dependencyAndExternalPathsRecursive( dependency, mutablePaths );
		return mutablePaths;
	}

	private static void dependencyAndExternalPathsRecursive( DiscoveryModule discoveryModule, Collection<Path> mutableDependencyPaths )
	{
		mutableDependencyPaths.addAll( discoveryModule.outputPaths() );
		mutableDependencyPaths.addAll( discoveryModule.externalDependencyPaths() );
		for( DiscoveryModule dependency : discoveryModule.projectDependencies() )
			dependencyAndExternalPathsRecursive( dependency, mutableDependencyPaths );
	}
}
