package io.github.mikenakis.testana.structure;

import io.github.mikenakis.debug.Debug;
import io.github.mikenakis.kit.GenericException;
import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.kit.logging.Log;
import io.github.mikenakis.testana.StructureSettings;
import io.github.mikenakis.testana.TestEngine;
import io.github.mikenakis.testana.discovery.Discoverer;
import io.github.mikenakis.testana.discovery.DiscoveryModule;
import io.github.mikenakis.testana.discovery.OutputDirectory;
import io.github.mikenakis.testana.discovery.OutputFile;
import io.github.mikenakis.testana.kit.TimeMeasurement;
import io.github.mikenakis.testana.structure.cache.Cache;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * {@link ProjectStructure} builder.
 *
 * @author michael.gr
 */
public final class ProjectStructureBuilder
{
	private static final String classExtension = ".class";

	private ProjectStructureBuilder()
	{
	}

	public static ProjectStructure build( Path sourceDirectory, Iterable<Discoverer> discoverers, StructureSettings settings, Cache cache, //
		Iterable<TestEngine> testEngines )
	{
		assert Kit.path.isAbsoluteNormalizedDirectory( sourceDirectory ) : sourceDirectory;
		Collection<DiscoveryModule> rootDiscoveryModules = collectRootDiscoveryModules( sourceDirectory, discoverers, settings );
		resolveDependencies( rootDiscoveryModules );

		Map<String,TestEngine> testEngineMap = Kit.stream.fromIterable( testEngines ).collect( Collectors.toMap( testEngine -> testEngine.name(), testEngine -> testEngine ) );

		LinkedHashMap<DiscoveryModule,ProjectModule> projectModuleMap = new LinkedHashMap<>();
		ProjectStructure projectStructure = new ProjectStructure( rootDiscoveryModules, projectModuleMap );
		TimeMeasurement.run( "Parsing types", "%d types (cache: %d hits, %d misses)", timeMeasurement -> //
		{
			for( DiscoveryModule discoveryModule : allDiscoveryModules( rootDiscoveryModules ) )
			{
				ClassLoader classLoader = createClassLoader( discoveryModule.allDependencyAndExternalPaths() );
				ByteCodeLoader byteCodeLoader = new ByteCodeLoader( classLoader ); //TODO: get rid of.
				Map<String,ProjectType> projectTypeFromNameMap = new LinkedHashMap<>();
				ProjectModule projectModule = new ProjectModule( projectStructure, discoveryModule, classLoader, byteCodeLoader, projectTypeFromNameMap );
				Kit.map.add( projectModuleMap, discoveryModule, projectModule );
				for( OutputDirectory outputDirectory : discoveryModule.outputDirectories() )
				{
					for( OutputFile outputFile : outputDirectory.files().stream().sorted( ProjectStructureBuilder::compareOutputFiles ).toList() )
					{
						String extension = Kit.path.getFileNameExtension( outputFile.relativePath );
						if( !extension.equals( classExtension ) )
						{
							Log.warning( "TODO: handle resources! (resource file: " + outputFile.relativePath + ")" );
							continue;
						}
						Optional<ProjectType> optionalProjectType = fromCache( cache, projectModule, outputDirectory, outputFile, testEngineMap ) //
							.or( () -> fromClass( classLoader, outputFile, testEngineMap, projectModule ) );
						optionalProjectType.ifPresent( projectType -> Kit.map.add( projectTypeFromNameMap, projectType.className(), projectType ) );
					}
				}
			}
			timeMeasurement.setArguments( projectStructure.projectModules().size(), projectStructure.typeCount(), cache.hits(), cache.misses() );
		} );

		return projectStructure;
	}

	//NOTE: this ensures that classes are loaded in the order in which the JVM would load them while executing them, that is, nested classes loaded _after_
	// containing classes. This is necessary because the IntellijIdea debugger relies on this order of loading; without this, the IntellijIdea debugger
	// gets confused, and breakpoints in nested classes do not hit. This was reported by me here: https://stackoverflow.com/q/70949498/773113 and became an
	// IntellijIdea issue here: https://youtrack.jetbrains.com/issue/IDEA-287858
	private static int compareOutputFiles( OutputFile a, OutputFile b )
	{
		if( a.type == OutputFile.Type.Class && b.type == OutputFile.Type.Class )
			return a.className().compareTo( b.className() );
		return a.relativePath.compareTo( b.relativePath );
	}

	private static Optional<ProjectType> fromClass( ClassLoader classLoader, OutputFile outputFile, Map<String,TestEngine> testEngineMap, ProjectModule projectModule )
	{
		return tryLoadClass( classLoader, outputFile.className() ).map( jvmClass -> //
		{
			Optional<TestEngine> testEngine = findTestEngine( jvmClass, testEngineMap.values() );
			return new ProjectType( projectModule, outputFile, testEngine, Optional.empty() );
		} );
	}

	private static Optional<Class<?>> tryLoadClass( ClassLoader classLoader, String className )
	{
		try
		{
			Class<?> jvmClass = classLoader.loadClass( className );
			return Optional.of( jvmClass );
		}
		catch( Throwable e )
		{
			Log.warning( "could not load class '" + className + "': " + e.getClass() + ": " + e.getMessage() );
			Debug.breakPoint();
			return Optional.empty();
		}
	}

	private static ClassLoader createClassLoader( Collection<Path> classPath )
	{
		URL[] urls = classPath.stream().map( path -> Kit.unchecked( () -> path.toUri().toURL() ) ).toArray( URL[]::new );
		return new URLClassLoader( urls, ClassLoader.getPlatformClassLoader() );
	}

	private static Collection<DiscoveryModule> allDiscoveryModules( Iterable<DiscoveryModule> rootDiscoveryModules )
	{
		Collection<DiscoveryModule> allDiscoveryModules = new LinkedHashSet<>();
		for( DiscoveryModule discoveryModule : rootDiscoveryModules )
			allDiscoveryModulesRecursive( discoveryModule, allDiscoveryModules );
		return allDiscoveryModules;
	}

	private static void allDiscoveryModulesRecursive( DiscoveryModule parent, Collection<DiscoveryModule> discoveryModules )
	{
		Kit.collection.add( discoveryModules, parent );
		for( DiscoveryModule discoveryModule : parent.nestedModules() )
			allDiscoveryModulesRecursive( discoveryModule, discoveryModules );
	}

	private static void resolveDependencies( Iterable<DiscoveryModule> rootDiscoveryModules )
	{
		Map<String,DiscoveryModule> nameToDiscoveryModuleMap = new LinkedHashMap<>();
		for( DiscoveryModule discoveryModule : rootDiscoveryModules )
			addRecursive( nameToDiscoveryModuleMap, discoveryModule );
		for( DiscoveryModule discoveryModule : rootDiscoveryModules )
			discoveryModule.resolveDependencies( nameToDiscoveryModuleMap );
		for( DiscoveryModule discoveryModule : nameToDiscoveryModuleMap.values() )
			if( discoveryModule.detectCycles() )
				throw new GenericException( "circular dependency detected" );
	}

	private static Collection<DiscoveryModule> collectRootDiscoveryModules( Path projectSourceDirectory, Iterable<Discoverer> discoverers, //
		StructureSettings settings )
	{
		return TimeMeasurement.run( "Looking for modules under " + projectSourceDirectory, "Found %d root modules, %d leaf modules with a total of %d classes, %d resources", timeMeasurement -> //
		{
			Collection<DiscoveryModule> discoveryModules = new LinkedHashSet<>();
			if( !collectDiscoveryModulesRecursive( projectSourceDirectory, discoverers, settings, discoveryModules ) )
				Log.error( "No projects found." );
			int resourceFileCount = countFilesRecursive( discoveryModules, OutputFile.Type.Resource );
			int classFileCount = countFilesRecursive( discoveryModules, OutputFile.Type.Class );
			int leafModuleCount = countLeafModulesRecursive( discoveryModules );
			timeMeasurement.setArguments( discoveryModules.size(), leafModuleCount, classFileCount, resourceFileCount );
			return discoveryModules;
		} );
	}

	private static boolean collectDiscoveryModulesRecursive( Path directory, Iterable<Discoverer> discoverers, //
		StructureSettings settings, Collection<DiscoveryModule> discoveryModules )
	{
		Optional<DiscoveryModule> discoveryModule = tryCollectDiscoveryModule( directory, discoverers );
		if( discoveryModule.isPresent() )
		{
			Kit.collection.add( discoveryModules, discoveryModule.get() );
			return true;
		}
		boolean foundSomething = false;
		for( Path subDirectory : Kit.path.enumerateSubDirectories( directory ) )
		{
			assert subDirectory.startsWith( directory );
			if( settings.shouldExcludeDirectory( subDirectory ) )
				continue;
			if( collectDiscoveryModulesRecursive( subDirectory, discoverers, settings, discoveryModules ) )
				foundSomething = true;
		}
		if( !foundSomething )
			Log.info( "Directory does not contain any modules: " + directory );
		return foundSomething;
	}

	private static Optional<DiscoveryModule> tryCollectDiscoveryModule( Path directory, Iterable<Discoverer> discoverers )
	{
		for( Discoverer discoverer : discoverers )
		{
			Optional<DiscoveryModule> module = discoverer.discover( directory );
			if( module.isPresent() )
				return module;
		}
		return Optional.empty();
	}

	private static void addRecursive( Map<String,DiscoveryModule> nameToModuleMap, DiscoveryModule module )
	{
		Kit.map.add( nameToModuleMap, module.name(), module );
		for( DiscoveryModule nestedModule : module.nestedModules() )
			addRecursive( nameToModuleMap, nestedModule );
	}

	private static int countLeafModulesRecursive( Iterable<DiscoveryModule> modules )
	{
		int count = 0;
		for( DiscoveryModule module : modules )
			count += countLeafModulesRecursive( module.nestedModules() );
		if( count == 0 )
			return 1;
		return count;
	}

	private static int countFilesRecursive( Iterable<DiscoveryModule> modules, OutputFile.Type type )
	{
		int count = 0;
		for( DiscoveryModule module : modules )
		{
			count += countFilesRecursive( module.nestedModules(), type );
			for( OutputDirectory outputDirectory : module.outputDirectories() )
				for( OutputFile outputFile : outputDirectory.files() )
					if( outputFile.type == type )
						count++;
		}
		return count;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private static Optional<ProjectType> fromCache( Cache cache, ProjectModule projectModule, OutputDirectory outputDirectory, OutputFile outputFile, //
		Map<String,TestEngine> testEngines )
	{
		return cache.tryGetType( projectModule.name(), outputDirectory, outputFile.className(), outputFile.lastModifiedTime ).map( c -> //
		{
			Optional<TestEngine> testEngine = c.testEngineName.map( name -> Kit.map.get( testEngines, name ) );
			return new ProjectType( projectModule, outputFile, testEngine, Optional.of( c.dependencyNames ) );
		} );
	}

	private static Optional<TestEngine> findTestEngine( Class<?> javaClass, Iterable<TestEngine> testEngines )
	{
		for( TestEngine testEngine : testEngines )
			if( testEngine.isTestClass( javaClass ) )
				return Optional.of( testEngine );
		return Optional.empty();
	}
}
