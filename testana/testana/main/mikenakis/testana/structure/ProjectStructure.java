package mikenakis.testana.structure;

import mikenakis.kit.Kit;
import mikenakis.testana.discovery.DiscoveryModule;
import mikenakis.testana.discovery.OutputDirectory;
import mikenakis.testana.discovery.OutputFile;
import mikenakis.testana.kit.TestanaLog;
import mikenakis.testana.kit.textTree.Branch;
import mikenakis.testana.kit.textTree.TextTree;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Contains all {@link ProjectType}s.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ProjectStructure
{
	public enum ShowOption
	{
		None,
		Terse,
		Medium,
		Verbose
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

	public void show( ShowOption showStructureOption )
	{
		if( showStructureOption != ShowOption.None )
		{
			TextTree textTree = new TextTree( "Project Structure", TestanaLog::report );
			textTree.print( rootDiscoveryModules, projectModule -> "Module: " + projectModule.name(), ( parent, projectModule ) -> handleModule( parent, projectModule, showStructureOption ) );
		}
	}

	private static void handleModule( TextTree textTree, DiscoveryModule parentModule, ShowOption showOption )
	{
		try( Branch branch = new Branch( textTree ) )
		{
			if( showOption.ordinal() > ShowOption.Terse.ordinal() )
			{
				branch.add( parentModule.projectDependencies(), projectDependency -> "Project Dependency: " + projectDependency.name() );
				if( showOption.ordinal() > ShowOption.Medium.ordinal() )
				{
					branch.add( parentModule.externalDependencyPaths(), externalDependency -> "External dependency: " + externalDependency.toString() );
					BiConsumer<TextTree,OutputDirectory> outputDirectoryBreeder = showOption == ShowOption.Verbose ? ProjectStructure::handleOutputDirectory : null;
					branch.add( parentModule.outputDirectories(), outputDirectory -> "Output Directory: " + outputDirectory.path, outputDirectoryBreeder );
				}
			}
			branch.add( parentModule.nestedModules(), nestedModule -> "SubModule: " + nestedModule.name(), ( parentTextTree, childModule ) -> handleModule( parentTextTree, childModule, showOption ) );
		}
	}

	private static void handleOutputDirectory( TextTree textTree, OutputDirectory outputDirectory )
	{
		textTree.print( outputDirectory.files(), OutputFile::toString );
	}

	public Collection<ProjectModule> projectModules()
	{
		return projectModuleMap.values();
	}

	int typeCount()
	{
		return projectModuleMap.values().stream().map( m -> m.getProjectTypes().size() ).reduce( 0, ( a, b ) -> a + b );
	}
}
