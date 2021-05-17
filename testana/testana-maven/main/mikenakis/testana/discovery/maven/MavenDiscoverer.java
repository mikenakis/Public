package mikenakis.testana.discovery.maven;

import mikenakis.kit.Kit;
import mikenakis.kit.logging.Log;
import mikenakis.testana.discovery.Discoverer;
import mikenakis.testana.discovery.DiscoveryModule;
import mikenakis.testana.discovery.OutputDirectory;
import mikenakis.testana.discovery.OutputFile;
import mikenakis.testana.kit.TestanaLog;
import org.apache.maven.model.Model;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/**
 * Maven {@link Discoverer}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class MavenDiscoverer implements Discoverer
{
	private final Path localRepositoryPath;
	private final MavenHelper mavenHelper;

	public MavenDiscoverer()
	{
		localRepositoryPath = getLocalRepositoryPath();
		TestanaLog.report( "Maven Local Repository Path: " + localRepositoryPath );
		mavenHelper = new MavenHelper( localRepositoryPath );
	}

	@Override public Optional<DiscoveryModule> discover( Path sourceDirectory )
	{
		assert Kit.path.isAbsoluteNormalizedDirectory( sourceDirectory );
		Path pomPathName = sourceDirectory.resolve( "pom.xml" );
		if( !Files.exists( pomPathName ) )
			return Optional.empty();
		return Optional.of( discoverRecursively( pomPathName.toFile() ) );
	}

	private MavenDiscoveryModule discoverRecursively( File pomFile )
	{
		Model mavenModel = mavenHelper.loadMavenModel( pomFile );
		MavenDiscoveryModule discoveryModule = new MavenDiscoveryModule( localRepositoryPath, mavenModel );
		boolean haveMain = mavenModel.getBuild() != null && addOutputDirectory( discoveryModule, mavenModel.getBuild().getOutputDirectory() );
		boolean haveTest = mavenModel.getBuild() != null && addOutputDirectory( discoveryModule, mavenModel.getBuild().getTestOutputDirectory() );
		if( !haveMain && !haveTest && !mavenModel.getPackaging().equals( "pom" ) )
			Log.warning( "Neither output nor test-output directories exist in " + mavenModel );
		for( String childModuleName : mavenModel.getModules() )
		{
			File childModulePomFile = mavenModel.getProjectDirectory().toPath().resolve( childModuleName ).resolve( "pom.xml" ).toFile();
			if( !childModulePomFile.exists() )
			{
				Log.warning( "Project '" + discoveryModule.toString() + "' (" + mavenModel.getPomFile() + ") references non-existent module '" + childModuleName + "' (" + childModulePomFile + ")" );
				continue;
			}
			MavenDiscoveryModule nestedDiscoveryModule = discoverRecursively( childModulePomFile );
			discoveryModule.addNestedModule( nestedDiscoveryModule );
		}
		return discoveryModule;
	}

	private static boolean addOutputDirectory( MavenDiscoveryModule discoveryModule, String outputDirectoryString )
	{
		if( outputDirectoryString == null )
			return false;
		Path outputPath = Paths.get( outputDirectoryString );
		if( !Files.exists( outputPath ) )
			return false;

		Collection<OutputFile> projectFiles = new ArrayList<>();
		for( Path pathName : Kit.path.enumerateFiles( outputPath ) )
		{
			assert pathName.startsWith( outputPath );
			String outputRelativePath = outputPath.relativize( pathName ).toString().replace( File.separatorChar, '/' );
			String extension = Kit.path.getFileNameExtension( pathName );
			OutputFile.Type type = extension.equals( ".class" )? OutputFile.Type.Class : OutputFile.Type.Resource;
			Instant lastModifiedTime = Kit.unchecked( () -> Files.getLastModifiedTime( pathName ) ).toInstant();
			OutputFile projectFile = new OutputFile( type, outputRelativePath, lastModifiedTime );
			Kit.collection.add( projectFiles, projectFile );
		}

		OutputDirectory outputDirectory = new OutputDirectory( outputPath, projectFiles );
		discoveryModule.addOutputDirectory( outputDirectory );
		return true;
	}

	private static Path getLocalRepositoryPath()
	{
		Path m2Path = Paths.get( System.getProperty( "user.home" ), ".m2" );
		// XXX See MVN-TODO-1
		//		Path mavenSettingsPath = m2Path.resolve( "settings.xml" );
		//      extract <localRepository>...</localRepository> from settings.xml
		return m2Path.resolve( "repository" );
	}
}
