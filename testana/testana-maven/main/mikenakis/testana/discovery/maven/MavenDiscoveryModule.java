package mikenakis.testana.discovery.maven;

import mikenakis.kit.Kit;
import mikenakis.kit.logging.Log;
import mikenakis.testana.discovery.DiscoveryModule;
import mikenakis.testana.discovery.OutputDirectory;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * Represents a Maven {@link DiscoveryModule}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
final class MavenDiscoveryModule extends DiscoveryModule
{
	private final Path localRepositoryPath;
	private final Collection<OutputDirectory> outputDirectories = new LinkedHashSet<>();
	private final Collection<MavenDiscoveryModule> nestedModules = new LinkedHashSet<>();
	private final Model mavenModel;
	private final Collection<DiscoveryModule> projectDependencies = new LinkedHashSet<>();
	private final Collection<Path> externalDependencies = new LinkedHashSet<>();
	private boolean dependenciesResolved; // TODO FIXME restructure things so that it is unnecessary to have this state.

	MavenDiscoveryModule( Path localRepositoryPath, Model mavenModel )
	{
		this.localRepositoryPath = localRepositoryPath;
		this.mavenModel = mavenModel;
	}

	@Override public String name()
	{
		return mavenModel.getId();
	}

	@Override public Path sourcePath()
	{
		return mavenModel.getProjectDirectory().toPath();
	}

	@Override public Collection<OutputDirectory> outputDirectories()
	{
		return outputDirectories;
	}

	@Override public Collection<DiscoveryModule> projectDependencies()
	{
		assert dependenciesResolved;
		return projectDependencies;
	}

	@Override public Collection<Path> externalDependencyPaths()
	{
		assert dependenciesResolved;
		return externalDependencies;
	}

	@Override public Collection<DiscoveryModule> nestedModules()
	{
		return new ArrayList<>( nestedModules );
	}

	@Override public String toString()
	{
		String pomFileName = mavenModel.getPomFile().toURI().toString();
		final String prefix = "file:/";
		assert pomFileName.startsWith( prefix );
		pomFileName = "file:///" + pomFileName.substring( prefix.length() );
		return mavenModel.getGroupId() + ":" + mavenModel.getArtifactId() + " (" + pomFileName + ")";
	}

	void addNestedModule( MavenDiscoveryModule nestedModule )
	{
		assert nestedModule != null;
		Kit.collection.add( nestedModules, nestedModule );
	}

	void addOutputDirectory( OutputDirectory outputDirectory )
	{
		assert outputDirectory != null;
		Kit.collection.add( outputDirectories, outputDirectory );
	}

	@Override public void resolveDependencies( Map<String,DiscoveryModule> nameToModuleMap )
	{
		assert !dependenciesResolved;
		dependenciesResolved = true;
		for( Dependency mavenDependency : mavenModel.getDependencies() )
		{
			if( !isMavenDependencyOfInterest( mavenDependency ) )
			{
				assert false; //does this happen?
				continue;
			}
			String dependencyType = mavenDependency.getType();
			switch( dependencyType )
			{
				case "jar":
				case "test-jar":
					String name = getMavenId( mavenDependency );
					if( nameToModuleMap.containsKey( name ) )
					{
						DiscoveryModule dependency = Kit.map.get( nameToModuleMap, name );
						Kit.collection.add( projectDependencies, dependency );
					}
					else
					{
						Path dependencyModulePathInLocalRepository = buildModulePathInLocalRepository( localRepositoryPath, mavenDependency );
						if( !Files.isDirectory( dependencyModulePathInLocalRepository ) )
						{
							Log.warning( "Directory " + dependencyModulePathInLocalRepository + " does not exist." );
							continue; //this has been observed when, for example, a parent pom references a non-existent child pom.
						}
						Path jarPathName = dependencyModulePathInLocalRepository.resolve( buildJarFileName( mavenDependency ) );
						if( !Files.exists( jarPathName ) )
						{
							assert false; //does this happen?
							continue; //this has been observed, for example, with commons-logging:commons-logging:1.0.4 depending on logkit:logkit:1.0.1 which consists of nothing but a pom.
						}
						assert jarPathName != null;
						Kit.collection.add( externalDependencies, jarPathName );
					}
					break;
				default:
					assert false : dependencyType;
					break;
			}
		}
		for( MavenDiscoveryModule nestedModule : nestedModules )
			nestedModule.resolveDependencies( nameToModuleMap );
	}

	private static String getMavenId( Dependency mavenDependency )
	{
		return mavenDependency.getGroupId() + ":" + mavenDependency.getArtifactId() + ":" + mavenDependency.getType() + ":" + mavenDependency.getVersion();
	}

	private static boolean isMavenDependencyOfInterest( Dependency mavenDependency )
	{
		String mavenDependencyScope = mavenDependency.getScope();
		if( mavenDependencyScope == null )
		{
			assert false; //does this ever happen?
			return true;
		}
		switch( mavenDependencyScope )
		{
			case "compile", "test":
			case "provided":
				return true;
			case "runtime":
				return true; //TODO: try making this 'false'
			case "system":
				assert false; //does this ever happen?
				return false;
			case "import": //not implemented
			default: //unknown dependency scope
				assert false : mavenDependencyScope;
				return false;
		}
	}

	private static String buildJarFileName( Dependency mavenDependency )
	{
		var builder = new StringBuilder();
		builder.append( mavenDependency.getArtifactId() );
		builder.append( '-' );
		builder.append( mavenDependency.getVersion() );
		String classifier = mavenDependency.getClassifier();
		if( classifier != null )
			builder.append( '-' ).append( classifier );
		builder.append( ".jar" );
		return builder.toString();
	}

	private static Path buildModulePathInLocalRepository( Path localRepositoryPath, Dependency mavenDependency )
	{
		String groupPath = mavenDependency.getGroupId().replace( '.', '/' );
		Path path = localRepositoryPath.resolve( groupPath );
		path = path.resolve( mavenDependency.getArtifactId() );
		if( mavenDependency.getVersion() != null )
			path = path.resolve( mavenDependency.getVersion() );
		return path;
	}
}
