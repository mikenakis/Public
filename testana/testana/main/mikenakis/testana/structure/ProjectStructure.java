package mikenakis.testana.structure;

import mikenakis.kit.Kit;
import mikenakis.kit.functional.Function1;
import mikenakis.testana.discovery.DiscoveryModule;
import mikenakis.testana.discovery.OutputDirectory;
import mikenakis.testana.discovery.OutputFile;
import mikenakis.testana.kit.TestanaLog;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Contains all {@link ProjectType}s.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ProjectStructure
{
	public enum ShowOption
	{
		None, Terse, Medium, Verbose
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
				final DiscoveryModule discoveryModule;
				ProjectDependency( DiscoveryModule discoveryModule ) { this.discoveryModule = discoveryModule; }
			}

			Function1<Iterable<Object>,Object> breeder = object -> //
			{
				if( object instanceof ProjectStructure projectStructure )
					return Kit.iterable.downCast( projectStructure.rootDiscoveryModules );
				if( object instanceof DiscoveryModule discoveryModule )
				{
					Collection<Object> children = new ArrayList<>();
					if( showOption.ordinal() > ShowOption.Terse.ordinal() )
					{
						if( !discoveryModule.projectDependencies().isEmpty() )
							for( var dependency : discoveryModule.projectDependencies() )
								children.add( new ProjectDependency( dependency ) );
						if( showOption.ordinal() > ShowOption.Medium.ordinal() )
						{
							if( !discoveryModule.externalDependencyPaths().isEmpty() )
								children.addAll( discoveryModule.externalDependencyPaths() );
							if( !discoveryModule.outputDirectories().isEmpty() )
								children.addAll( discoveryModule.outputDirectories() );
						}
					}
					if( !discoveryModule.nestedModules().isEmpty() )
						children.addAll( discoveryModule.nestedModules() );
					return children;
				}
				if( object instanceof ProjectDependency )
					return List.of();
				if( object instanceof Path )
					return List.of();
				if( object instanceof OutputDirectory outputDirectory )
					return Kit.iterable.downCast( outputDirectory.files() );
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

	public int typeCount()
	{
		return projectModuleMap.values().stream().map( m -> m.getProjectTypes().size() ).reduce( 0, ( a, b ) -> a + b );
	}
}
