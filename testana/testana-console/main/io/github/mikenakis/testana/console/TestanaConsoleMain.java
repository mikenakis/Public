package io.github.mikenakis.testana.console;

import io.github.mikenakis.clio.Clio;
import io.github.mikenakis.testana.AncestryOrdering;
import io.github.mikenakis.testana.ClassOrdering;
import io.github.mikenakis.testana.MethodOrdering;
import io.github.mikenakis.testana.ModuleOrdering;
import io.github.mikenakis.testana.Persistence;
import io.github.mikenakis.testana.StructureSettings;
import io.github.mikenakis.testana.TestEngine;
import io.github.mikenakis.testana.console.textfont.Bitmap;
import io.github.mikenakis.testana.console.textfont.Font;
import io.github.mikenakis.testana.console.textfont.TinyFont;
import io.github.mikenakis.testana.discovery.Discoverer;
import io.github.mikenakis.testana.discovery.maven.MavenDiscoverer;
import io.github.mikenakis.testana.kit.TestanaLog;
import io.github.mikenakis.testana.kit.TimeMeasurement;
import io.github.mikenakis.testana.runtime.TestRunner;
import io.github.mikenakis.testana.runtime.result.TestResult;
import io.github.mikenakis.testana.structure.ProjectStructure;
import io.github.mikenakis.testana.structure.ProjectStructureBuilder;
import io.github.mikenakis.testana.structure.cache.Cache;
import io.github.mikenakis.testana.test_engines.junit.JunitTestEngine;
import io.github.mikenakis.testana.testplan.TestPlan;
import io.github.mikenakis.testana.testplan.TestPlanBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * Testana Launcher.
 * <p>
 * Make sure to run with `Working Directory = $PROJECT_DIR$` and `Before launch: Build Project` (not just `Before launch: Build` because that would only build
 * dependent modules, and testana does not dependent on your modules.)
 *
 * @author michael.gr
 */
public class TestanaConsoleMain
{
	public static void main( String[] commandLineArguments )
	{
		TestanaConsoleMain testanaConsoleMain = new TestanaConsoleMain();
		int result = testanaConsoleMain.run( commandLineArguments );
		System.exit( result );
	}

	private TestanaConsoleMain()
	{
	}

	private int run( String[] commandLineArguments )
	{
		Path defaultSourcesPath = Paths.get( "." ).toAbsolutePath().normalize();
		Path defaultSettingsPath = Paths.get( "./testana.settings" ).toAbsolutePath().normalize();
		Path defaultPersistencePath = Paths.get( "./.testana.persistence.json" ).toAbsolutePath().normalize();
		Path defaultCachePath = Paths.get( "./.testana.cache.json" ).toAbsolutePath().normalize();
		Clio clio = new Clio( "testana" );
		Supplier<Boolean> clearHistorySwitch = clio.addOptionalSwitchArgument( "--clear-history", "clear history of last successful run time of each test, so that all tests will run again." );
		Supplier<Boolean> clearCacheSwitch = clio.addOptionalSwitchArgument( "--clear-cache", "clear cache of class dependencies, so that it will be rebuilt from scratch." );
		Supplier<Boolean> noSaveSwitch = clio.addOptionalSwitchArgument( "--no-save", "do not save history and cache." );
		Supplier<ProjectStructure.ShowOption> showStructureOption = clio.addOptionalEnumNamedArgumentWithDefault( "--show-structure", ProjectStructure.ShowOption.class, ProjectStructure.ShowOption.None, "show project structure." );
		Supplier<TestPlan.ShowOption> showTestPlanOption = clio.addOptionalEnumNamedArgumentWithDefault( "--show-test-plan", TestPlan.ShowOption.class, TestPlan.ShowOption.None, "show the test plan." );
		Supplier<Boolean> loopOption = clio.addOptionalSwitchArgument( "--loop", "run in an endless loop (for profiling.)" );
		Supplier<Boolean> noRunOption = clio.addOptionalSwitchArgument( "--no-run", "do not run any tests. (Useful for displaying information and exiting.)" );
		Supplier<Boolean> noModuleOrder = clio.addOptionalSwitchArgument( "--no-module-order", "do not order test modules by dependency." );
		Supplier<Boolean> noClassOrder = clio.addOptionalSwitchArgument( "--no-class-order", "do not order test classes by dependency." );
		Supplier<Boolean> noMethodOrder = clio.addOptionalSwitchArgument( "--no-method-order", "do not order test methods by source line number." );
		Supplier<Boolean> noAncestryOrder = clio.addOptionalSwitchArgument( "--no-ancestry-order", "do not run test methods of ancestor classes first." );
		Supplier<Path> projectSourceRootDirectoryArgument = clio.addOptionalPathPositionalArgumentWithDefault( "project-source-root-directory", defaultSourcesPath, "the root source directory of the project." );
		Supplier<Path> settingsPathNameArgument = clio.addOptionalPathPositionalArgumentWithDefault( "settings-pathname", defaultSettingsPath, "the pathname of the settings file." );
		Supplier<Path> persistencePathNameArgument = clio.addOptionalPathPositionalArgumentWithDefault( "persistence-pathname", defaultPersistencePath, "the pathname of the persistence file." );
		Supplier<Path> cachePathNameArgument = clio.addOptionalPathPositionalArgumentWithDefault( "cache-pathname", defaultCachePath, "the pathname of the cache file." );
		if( !clio.parse( commandLineArguments ) )
			return -1;

		boolean shouldExit = false;

		if( clearHistorySwitch.get() )
		{
			Persistence persistence = new Persistence( persistencePathNameArgument.get(), true, false );
			persistence.save();
			TestanaLog.report( "History cleared." );
			shouldExit = true;
		}

		if( clearCacheSwitch.get() )
		{
			Cache cache = Cache.empty();
			cache.save( cachePathNameArgument.get() );
			TestanaLog.report( "Cache cleared." );
			shouldExit = true;
		}

		if( shouldExit )
			return 0;

		for( ; ; )
		{
			run1( projectSourceRootDirectoryArgument.get(), settingsPathNameArgument.get(), persistencePathNameArgument.get(), cachePathNameArgument.get(), //
				showStructureOption.get(), showTestPlanOption.get(), noRunOption.get(), noSaveSwitch.get(), //
				noModuleOrder.get() ? ModuleOrdering.None : ModuleOrdering.ByDependency, //
				noClassOrder.get() ? ClassOrdering.None : ClassOrdering.ByDependency, //
				noMethodOrder.get() ? MethodOrdering.Alphabetic : MethodOrdering.Natural, //
				noAncestryOrder.get() ? AncestryOrdering.Backwards : AncestryOrdering.Normal );
			if( !(boolean)loopOption.get() )
				break;
		}

		return 0;
	}

	private static void run1( Path sourceDirectory, Path configurationPathName, Path persistencePathName, Path cachePathName, //
		ProjectStructure.ShowOption showStructureOption, TestPlan.ShowOption showTestPlanOption, boolean noRun, boolean noSave, //
		ModuleOrdering moduleOrdering, ClassOrdering classOrdering, MethodOrdering methodOrdering, AncestryOrdering ancestryOrdering )
	{
		StructureSettings structureSettings = new StructureSettings();
		structureSettings.load( sourceDirectory, configurationPathName );

		Persistence persistence = new Persistence( persistencePathName, false, true );

		Collection<TestEngine> testEngines = List.of( new JunitTestEngine( methodOrdering, ancestryOrdering ) );

		Collection<Discoverer> discoverers = List.of( new MavenDiscoverer() );

		Cache cache = loadCache( cachePathName );

		ProjectStructure projectStructure = ProjectStructureBuilder.build( sourceDirectory, discoverers, structureSettings, cache, testEngines );
		if( cache.hits() != projectStructure.typeCount() && !noSave )
			saveCache( cachePathName, projectStructure );
		projectStructure.show( showStructureOption );

		TestPlan testPlan = TestPlanBuilder.build( persistence, projectStructure, moduleOrdering, classOrdering );
		testPlan.show( showTestPlanOption );

		if( !noRun )
		{
			TestResult testResult = TestRunner.run( testPlan, persistence );
			TestanaLog.report( "-".repeat( 80 ) );
			TestanaLog.report( testResult.getOutcomeMessage() );
			boolean success = testResult.failureCount() == 0;
			String message = " " + (success ? "Success (" + testResult.successCount() + "/" + testResult.totalCount() + ")" : "Failure") + " ";
			renderText( message );
			TestanaLog.report( "-".repeat( 80 ) );
		}

		if( persistence.isDirty() && !noSave )
			persistence.save();

		structureSettings.reportWarnings();
	}

	private static void renderText( String message )
	{
		for( String line : linesFromText( message ) )
			TestanaLog.report( line );
	}

	private static List<String> linesFromText( String message )
	{
		Bitmap bitmap = bitmapFromText( message );
		return bitmap.toStrings();
	}

	private static Bitmap bitmapFromText( String message )
	{
		Font font = TinyFont.create();
		return font.render( message );
	}

	private static Cache loadCache( Path cachePathName )
	{
		if( !Files.exists( cachePathName ) )
			return Cache.empty();
		try
		{
			return TimeMeasurement.run( "Loading cache from '" + cachePathName + "'", "Loaded %d modules, %d types from cache", timeMeasurement -> //
			{
				Cache cache = Cache.fromFile( cachePathName );
				timeMeasurement.setArguments( cache.moduleCount(), cache.typeCount() );
				return cache;
			} );
		}
		catch( RuntimeException | Error e )
		{
			TestanaLog.report( "Cache could not be loaded." );
			return Cache.empty();
		}
	}

	private static void saveCache( Path cachePathName, ProjectStructure projectStructure )
	{
		TimeMeasurement.run( "Saving cache", "Saved %d modules, %d types into cache", timeMeasurement -> //
		{
			Cache cache = Cache.fromProjectStructure( projectStructure );
			cache.save( cachePathName );
			timeMeasurement.setArguments( cache.moduleCount(), cache.typeCount() );
		} );
	}
}
