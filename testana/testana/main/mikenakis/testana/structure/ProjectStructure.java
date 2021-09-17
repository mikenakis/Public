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

	public void show( ShowOption showOption )
	{
		if( showOption != ShowOption.None )
		{
			TextTree textTree = new TextTree( "Project Structure", TestanaLog::report );
			textTree.print( rootDiscoveryModules, discoveryModule -> "Module: " + discoveryModule.name(), //
				( parent, discoveryModule ) -> showModule( parent, discoveryModule, showOption ) );
		}
	}

	private static void showModule( TextTree textTree, DiscoveryModule discoveryModule, ShowOption showOption )
	{
		// TODO FIXME this is intractable. Branch.close() does stuff. Simplify this.
		try( Branch branch = new Branch( textTree ) )
		{
			if( showOption.ordinal() > ShowOption.Terse.ordinal() )
			{
				branch.add( discoveryModule.projectDependencies(), projectDependency -> "Project Dependency: " + projectDependency.name() );
				if( showOption.ordinal() > ShowOption.Medium.ordinal() )
				{
					branch.add( discoveryModule.externalDependencyPaths(), externalDependency -> "External dependency: " + externalDependency.toString() );
					BiConsumer<TextTree,OutputDirectory> outputDirectoryBreeder = showOption == ShowOption.Verbose ? ProjectStructure::showOutputDirectory : null;
					branch.add( discoveryModule.outputDirectories(), outputDirectory -> "Output Directory: " + outputDirectory.path, outputDirectoryBreeder );
				}
			}
			branch.add( discoveryModule.nestedModules(), nestedModule -> "SubModule: " + nestedModule.name(), ( parentTextTree, childModule ) -> showModule( parentTextTree, childModule, showOption ) );
		}
	}

	private static void showOutputDirectory( TextTree textTree, OutputDirectory outputDirectory )
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
