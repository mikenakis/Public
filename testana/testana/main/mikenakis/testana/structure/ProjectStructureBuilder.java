package mikenakis.testana.structure;

import mikenakis.kit.Kit;
import mikenakis.kit.logging.Log;
import mikenakis.services.bytecode.ByteCodeService;
import mikenakis.testana.StructureSettings;
import mikenakis.testana.TestEngine;
import mikenakis.testana.discovery.Discoverer;
import mikenakis.testana.discovery.DiscoveryModule;
import mikenakis.testana.discovery.OutputDirectory;
import mikenakis.testana.discovery.OutputFile;
import mikenakis.testana.kit.TimeMeasurement;
import mikenakis.testana.kit.TestanaLog;
import mikenakis.testana.structure.cache.Cache;
import mikenakis.testana.structure.cache.CacheType;

import java.nio.file.Files;
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
 * @author Michael Belivanakis (michael.gr)
 */
public final class ProjectStructureBuilder
{
	private static final String classExtension = ".class";

	private ProjectStructureBuilder()
	{
	}

	public static ProjectStructure build( Path sourceDirectory, Iterable<Discoverer> discoverers, //
		StructureSettings settings, Optional<Path> cachePathName, Iterable<TestEngine> testEngines )
	{
		assert Kit.path.isAbsoluteNormalizedDirectory( sourceDirectory ) : sourceDirectory;
		Collection<DiscoveryModule> rootDiscoveryModules = collectRootDiscoveryModules( sourceDirectory, discoverers, settings );
		resolveDependencies( rootDiscoveryModules );

		Map<String,TestEngine> testEngineMap = Kit.collection.stream.fromIterable( testEngines ).collect( Collectors.toMap( testEngine -> testEngine.name(), testEngine -> testEngine ) );

		LinkedHashMap<DiscoveryModule,ProjectModule> projectModuleMap = new LinkedHashMap<>();
		ProjectStructure projectStructure = new ProjectStructure( rootDiscoveryModules, projectModuleMap );
		Cache cache = loadCache( cachePathName );
		TimeMeasurement.run( "Parsing types", "%d types (cache: %d hits, %d misses)", timeMeasurement -> //
		{
			for( DiscoveryModule discoveryModule : allDiscoveryModules( rootDiscoveryModules ) )
			{
				ClassAndByteCodeLoader classAndByteCodeLoader = new ClassAndByteCodeLoader( discoveryModule.allOutputPaths() );
				Map<String,ProjectType> projectTypeFromNameMap = new LinkedHashMap<>();
				ProjectModule projectModule = new ProjectModule( projectStructure, discoveryModule, classAndByteCodeLoader, projectTypeFromNameMap );
				Kit.map.add( projectModuleMap, discoveryModule, projectModule );
				for( OutputDirectory outputDirectory : discoveryModule.outputDirectories() )
				{
					for( OutputFile outputFile : outputDirectory.files() )
					{
						String extension = Kit.path.getFileNameExtension( outputFile.relativePath );
						if( !extension.equals( classExtension ) )
						{
							Log.warning( "TODO: handle resources! (resource file: " + outputFile.relativePath + ")" );
						}
						else
						{
							ProjectType projectType = fromCache( cache, projectModule, outputDirectory, outputFile, testEngineMap );
							if( projectType == null )
							{
								Optional<Class<?>> jvmClass = classAndByteCodeLoader.tryGetClassByName( outputFile.className() );
								if( jvmClass.isEmpty() )
									continue;
								Optional<TestEngine> testEngine = findTestEngine( jvmClass.get(), testEngineMap.values() );
								projectType = new ProjectType( projectModule, outputFile, Optional.empty(), testEngine );
							}
							Kit.map.add( projectTypeFromNameMap, projectType.className(), projectType );
						}
					}
				}
			}
			timeMeasurement.setArguments( projectStructure.projectModules().size(), projectStructure.typeCount(), cache.hits(), cache.misses() );
		} );

		TestanaLog.report( "ByteCodeService: " + ByteCodeService.instance.getStatsString() );

		if( cachePathName.isPresent() && cache.hits() != projectStructure.typeCount() )
			saveCache( cachePathName, projectStructure );
		return projectStructure;
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
		Map<String,DiscoveryModule> nameToModuleMap = new LinkedHashMap<>();
		for( DiscoveryModule discoveryModule : rootDiscoveryModules )
			addRecursive( nameToModuleMap, discoveryModule );
		for( DiscoveryModule discoveryModule : rootDiscoveryModules )
			discoveryModule.resolveDependencies( nameToModuleMap );
	}

	private static Collection<DiscoveryModule> collectRootDiscoveryModules( Path projectSourceDirectory, Iterable<Discoverer> discoverers, //
		StructureSettings settings )
	{
		return TimeMeasurement.run( "Looking for modules under " + projectSourceDirectory, "Found %d root modules, %d leaf modules with a total of %d classes, %d resources", timeMeasurement -> {
			Collection<DiscoveryModule> rootModules = new LinkedHashSet<>();
			if( !discoverRootModulesRecursive( projectSourceDirectory, discoverers, settings, rootModules ) )
				Log.error( "No projects found." );
			int resourceFileCount = countFilesRecursive( rootModules, OutputFile.Type.Resource );
			int classFileCount = countFilesRecursive( rootModules, OutputFile.Type.Class );
			int leafModuleCount = countLeafModulesRecursive( rootModules );
			timeMeasurement.setArguments( rootModules.size(), leafModuleCount, classFileCount, resourceFileCount );
			return rootModules;
		} );
	}

	private static boolean discoverRootModulesRecursive( Path directory, Iterable<Discoverer> discoverers, //
		StructureSettings settings, Collection<DiscoveryModule> rootDiscoveryModules )
	{
		Optional<DiscoveryModule> module = discoverModule( directory, discoverers );
		if( module.isPresent() )
		{
			Kit.collection.add( rootDiscoveryModules, module.get() );
			return true;
		}
		boolean foundSomething = false;
		for( Path subDirectory : Kit.path.enumerateSubDirectories( directory ) )
		{
			assert subDirectory.startsWith( directory );
			if( settings.shouldExcludeDirectory( subDirectory ) )
				continue;
			if( discoverRootModulesRecursive( subDirectory, discoverers, settings, rootDiscoveryModules ) )
				foundSomething = true;
		}
		if( !foundSomething )
			Log.info( "Directory does not contain any modules: " + directory );
		return foundSomething;
	}

	private static Optional<DiscoveryModule> discoverModule( Path directory, Iterable<Discoverer> discoverers )
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

	private static ProjectType fromCache( Cache cache, ProjectModule projectModule, OutputDirectory outputDirectory, OutputFile outputFile, //
		Map<String,TestEngine> testEngines )
	{
		Optional<CacheType> cacheType = cache.tryGetType( projectModule.name(), outputDirectory, outputFile.className(), outputFile.lastModifiedTime );
		if( cacheType.isEmpty() )
			return null;
		Optional<TestEngine> testEngine = cacheType.get().testEngineName.map( name -> Kit.map.get( testEngines, name ) );
		return new ProjectType( projectModule, outputFile, Optional.of( cacheType.get().dependencyNames ), testEngine );
	}

	private static Cache loadCache( Optional<Path> cachePathName )
	{
		if( cachePathName.isEmpty() || !Files.exists( cachePathName.get() ) )
			return Cache.empty();
		return TimeMeasurement.run( "Loading cache from '" + cachePathName.get() + "'", "Loaded %d modules, %d types from cache", timeMeasurement -> //
		{
			Cache cache = Cache.fromFile( cachePathName.get() );
			timeMeasurement.setArguments( cache.moduleCount(), cache.typeCount() );
			return cache;
		} );
	}

	private static void saveCache( Optional<Path> cachePathName, ProjectStructure projectStructure )
	{
		if( cachePathName.isEmpty() )
			return;
		TimeMeasurement.run( "Saving cache", "Saved %d modules, %d types into cache", timeMeasurement -> //
		{
			Cache cache = Cache.fromProjectStructure( projectStructure );
			cache.save( cachePathName.get() );
			timeMeasurement.setArguments( cache.moduleCount(), cache.typeCount() );
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
