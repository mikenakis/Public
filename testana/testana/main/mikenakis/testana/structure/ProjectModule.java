package mikenakis.testana.structure;

import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.kit.Kit;
import mikenakis.testana.discovery.DiscoveryModule;
import mikenakis.testana.discovery.OutputDirectory;
import mikenakis.testana.discovery.OutputFile;

import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class ProjectModule
{
	private final ProjectStructure projectStructure;
	private final DiscoveryModule discoveryModule;
	private final ClassLoader classLoader;
	private final ByteCodeLoader classAndByteCodeLoader;
	private final Map<String,ProjectType> projectTypeFromNameMap;

	ProjectModule( ProjectStructure projectStructure, DiscoveryModule discoveryModule, ClassLoader classLoader, ByteCodeLoader classAndByteCodeLoader, //
		Map<String,ProjectType> projectTypeFromNameMap )
	{
		this.projectStructure = projectStructure;
		this.discoveryModule = discoveryModule;
		this.classLoader = classLoader;
		this.classAndByteCodeLoader = classAndByteCodeLoader;
		this.projectTypeFromNameMap = projectTypeFromNameMap;
	}

	public Path sourcePath()
	{
		return discoveryModule.sourcePath();
	}

	public Collection<ProjectType> getProjectTypes()
	{
		return projectTypeFromNameMap.values();
	}

	public Collection<ProjectModule> projectDependencies()
	{
		return discoveryModule.projectDependencies().stream().map( n -> projectStructure.projectModuleFromDiscoveryModule( n ) ).collect( Collectors.toList() );
	}

	public Collection<ProjectModule> allProjectDependencies()
	{
		Collection<ProjectModule> allDependencies = new LinkedHashSet<>();
		addDependenciesRecursive( projectDependencies(), allDependencies );
		return allDependencies;
	}

	private static void addDependenciesRecursive( Collection<ProjectModule> projectModules, Collection<ProjectModule> allDependencies )
	{
		allDependencies.addAll( projectModules );
		for( ProjectModule projectModule : projectModules )
			addDependenciesRecursive( projectModule.projectDependencies(), allDependencies );
	}

	public Optional<ProjectType> tryGetProjectTypeByName( String typeName )
	{
		return Optional.ofNullable( Kit.map.tryGet( projectTypeFromNameMap, typeName ) );
	}

	private Optional<ProjectModule> tryGetProjectModuleByTypeNameTransitively( String typeName )
	{
		Optional<ProjectType> ownProjectType = tryGetProjectTypeByName( typeName );
		if( ownProjectType.isPresent() )
			return Optional.of( this );
		for( ProjectModule projectModule : projectDependencies() )
		{
			Optional<ProjectModule> result = projectModule.tryGetProjectModuleByTypeNameTransitively( typeName );
			if( result.isPresent() )
				return result;
		}
//		{
//			Optional<ProjectType> projectType = projectModule.tryGetProjectTypeByName( typeName );
//			if( projectType.isPresent() )
//				return Optional.of( projectModule );
//		}
		return Optional.empty();
	}

	Optional<ProjectType> tryGetProjectTypeByNameTransitively( String typeName )
	{
		return tryGetProjectModuleByTypeNameTransitively( typeName ).flatMap( m -> m.tryGetProjectTypeByName( typeName ) );
	}

	ByteCodeType getProjectByteCodeTypeByNameTransitively( String typeName )
	{
		return tryGetProjectModuleByTypeNameTransitively( typeName ).map( m -> m.classAndByteCodeLoader.getByteCodeTypeByName( typeName ) ).orElseThrow();
	}

	Class<?> getProjectClassByNameTransitively( String typeName )
	{
		return tryGetProjectModuleByTypeNameTransitively( typeName ).map( m -> Kit.unchecked( () -> m.classLoader.loadClass( typeName ) ) ).orElseThrow();
	}

	boolean isProjectTypeTransitively( String typeName )
	{
		return tryGetProjectModuleByTypeNameTransitively( typeName ).isPresent();
	}

	public Optional<ProjectType> tryGetProjectTypeByOutputFile( OutputFile outputFile )
	{
		String className = outputFile.className();
		return Optional.ofNullable( Kit.map.tryGet( projectTypeFromNameMap, className ) );
	}

	public String name()
	{
		return discoveryModule.name();
	}

	public Iterable<? extends OutputDirectory> outputDirectories()
	{
		return discoveryModule.outputDirectories();
	}

	@Override public String toString()
	{
		return discoveryModule.toString();
	}

	public boolean dependsOn( ProjectModule other )
	{
		assert other != this;
		for( ProjectModule dependency : projectDependencies() )
		{
			if( dependency == other )
				return true;
			if( dependency.dependsOn( other ) )
				return true;
		}
		return false;
	}
}
