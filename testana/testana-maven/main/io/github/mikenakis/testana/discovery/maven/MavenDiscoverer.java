package io.github.mikenakis.testana.discovery.maven;

import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.kit.logging.Log;
import io.github.mikenakis.testana.discovery.Discoverer;
import io.github.mikenakis.testana.discovery.DiscoveryModule;
import io.github.mikenakis.testana.discovery.OutputDirectory;
import io.github.mikenakis.testana.discovery.OutputFile;
import io.github.mikenakis.testana.kit.TestanaLog;
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
 * @author michael.gr
 */
public final class MavenDiscoverer implements Discoverer
{
	private final MavenHelper mavenHelper;

	public MavenDiscoverer()
	{
		mavenHelper = new MavenHelper();
		TestanaLog.report( "Maven Local Repository Path: " + mavenHelper.localRepositoryPath );
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
		MavenDiscoveryModule discoveryModule = new MavenDiscoveryModule( mavenHelper, mavenModel );
		boolean haveMain = mavenModel.getBuild() != null && addOutputDirectory( discoveryModule, mavenModel.getBuild().getOutputDirectory() );
		boolean haveTest = mavenModel.getBuild() != null && addOutputDirectory( discoveryModule, mavenModel.getBuild().getTestOutputDirectory() );
		if( !haveMain && !haveTest && !mavenModel.getPackaging().equals( "pom" ) )
			Log.warning( "Neither output nor test-output files exist in " + mavenModel );
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
}
