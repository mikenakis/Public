package io.github.mikenakis.testana.structure;

import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.kit.functional.Function1;
import io.github.mikenakis.testana.discovery.DiscoveryModule;
import io.github.mikenakis.testana.discovery.OutputDirectory;
import io.github.mikenakis.testana.discovery.OutputFile;
import io.github.mikenakis.testana.kit.TestanaLog;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Contains all {@link ProjectType}s.
 *
 * @author michael.gr
 */
public final class ProjectStructure
{
	public enum ShowOption
	{
		None,    // Does not show the project structure.
		Terse,   // Shows the hierarchy of modules and submodules.
		Normal,  // For each module also shows its project dependencies.
		Verbose, // For each module also shows output directories and external dependencies.
		Spammy   // For each output directory also shows all classes in it.
	}

	private final Collection<DiscoveryModule> rootDiscoveryModules;
	private final Map<DiscoveryModule,ProjectModule> projectModuleMap;

	ProjectStructure( Collection<DiscoveryModule> rootDiscoveryModules, Map<DiscoveryModule,ProjectModule> projectModuleMap )
	{
		this.rootDiscoveryModules = rootDiscoveryModules;
		this.projectModuleMap = projectModuleMap;
	}

	ProjectModule projectModuleFromDiscoveryModule( DiscoveryModule discoveryModule )
	{
		return Kit.map.get( projectModuleMap, discoveryModule );
	}

	public void show( ShowOption showOption )
	{
		if( showOption != ShowOption.None )
		{
			class ProjectDependency
			{
				private final DiscoveryModule discoveryModule;
				private ProjectDependency( DiscoveryModule discoveryModule ) { this.discoveryModule = discoveryModule; }
			}

			Function1<Iterable<? extends Object>,Object> breeder = object -> //
			{
				if( object instanceof ProjectStructure projectStructure )
					return Kit.iterable.downCast( projectStructure.rootDiscoveryModules );
				if( object instanceof DiscoveryModule discoveryModule )
				{
					Collection<Object> children = new ArrayList<>();
					if( showOption.ordinal() >= ShowOption.Normal.ordinal() )
					{
						if( !discoveryModule.projectDependencies().isEmpty() )
							for( var dependency : discoveryModule.projectDependencies() )
								children.add( new ProjectDependency( dependency ) );
						if( showOption.ordinal() >= ShowOption.Verbose.ordinal() )
						{
							if( !discoveryModule.externalDependencyPaths().isEmpty() )
								children.addAll( discoveryModule.externalDependencyPaths() );
							if( !discoveryModule.outputDirectories().isEmpty() )
								children.addAll( discoveryModule.outputDirectories() );
						}
					}
					assert showOption.ordinal() >= ShowOption.Terse.ordinal();
					children.addAll( discoveryModule.nestedModules() );
					return children;
				}
				if( object instanceof ProjectDependency )
					return List.of();
				if( object instanceof Path )
					return List.of();
				if( object instanceof OutputDirectory outputDirectory )
				{
					if( showOption.ordinal() >= ShowOption.Spammy.ordinal() )
						return Kit.iterable.downCast( outputDirectory.files() );
					else
						return List.of();
				}
				if( object instanceof OutputFile )
					return List.of();
				assert false;
				return null;
			};
			Function1<String,Object> stringizer = object -> //
			{
				if( object instanceof ProjectStructure )
					return "Project Structure";
				if( object instanceof DiscoveryModule discoveryModule )
					return "Module: " + discoveryModule.name();
				if( object instanceof ProjectDependency projectDependency )
					return "Project Dependency: " + projectDependency.discoveryModule.name();
				if( object instanceof Path path )
					return "External Dependency: " + path.toString();
				if( object instanceof OutputDirectory outputDirectory )
					return "Output Directory: " + outputDirectory.path;
				if( object instanceof OutputFile outputFile )
					return outputFile.toString();
				assert false;
				return null;
			};
			Kit.tree.print( this, breeder, stringizer, TestanaLog::report );
		}
	}

	public Collection<ProjectModule> projectModules()
	{
		return projectModuleMap.values();
	}

	public ProjectType getProjectTypeByName( String typeName )
	{
		List<ProjectType> projectTypes = projectModules().stream().flatMap( m -> m.tryGetProjectTypeByName( typeName ).stream() ).toList();
		assert projectTypes.size() == 1;
		return projectTypes.get( 0 );
	}

	public int typeCount()
	{
		return projectModuleMap.values().stream().map( m -> m.getProjectTypes().size() ).reduce( 0, ( a, b ) -> a + b );
	}
}
