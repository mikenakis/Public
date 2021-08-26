package mikenakis.testana.console;

import mikenakis.clio.Clio;
import mikenakis.testana.AncestryOrdering;
import mikenakis.testana.ClassOrdering;
import mikenakis.testana.MethodOrdering;
import mikenakis.testana.ModuleOrdering;
import mikenakis.testana.Persistence;
import mikenakis.testana.StructureSettings;
import mikenakis.testana.TestEngine;
import mikenakis.testana.console.textfont.BitmapFontRenderer;
import mikenakis.testana.console.textfont.QuadrantTextFontRenderer;
import mikenakis.testana.console.textfont.TextFontRenderer;
import mikenakis.testana.console.textfont.Tiny2Font;
import mikenakis.testana.discovery.Discoverer;
import mikenakis.testana.discovery.maven.MavenDiscoverer;
import mikenakis.testana.kit.TimeMeasurement;
import mikenakis.testana.kit.TestanaLog;
import mikenakis.testana.runtime.TestRunner;
import mikenakis.testana.runtime.result.TestResult;
import mikenakis.testana.structure.ProjectStructure;
import mikenakis.testana.structure.ProjectStructureBuilder;
import mikenakis.testana.test_engines.junit.JunitTestEngine;
import mikenakis.testana.testplan.TestPlan;
import mikenakis.testana.testplan.TestPlanBuilder;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Testana Launcher.
 * <p>
 * Make sure to run with `Working Directory = $PROJECT_DIR$` and `Before launch: Build Project`
 * (not just `Before launch: Build` because that would only build dependent modules, and testana does not dependent on your modules.)
 *
 * @author Michael Belivanakis (michael.gr)
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
		Clio clio = new Clio( "testana" );
		Supplier<Boolean> resetHistorySwitch = clio.addSwitchOption( "--reset-history | -r", "reset history of last successful run time of each test, so that all tests run again." );
		Supplier<Boolean> doNotSaveHistorySwitch = clio.addSwitchOption( "--no-save-history | -n", "do not save history of last successful test run times." );
		Supplier<ProjectStructure.ShowOption> structureShowOption = clio.addEnumParameterOption( "--show-structure", "show-option", ProjectStructure.ShowOption.class, ProjectStructure.ShowOption.None, "show project structure." );
		Supplier<TestPlan.ShowOption> testPlanShowOption = clio.addEnumParameterOption( "--show-test-plan", "show-option", TestPlan.ShowOption.class, TestPlan.ShowOption.None, "show the test plan." );
		Supplier<Boolean> loopOption = clio.addSwitchOption( "--loop", "run in an endless loop (for profiling.)" );
		Supplier<Boolean> noRunOption = clio.addSwitchOption( "--no-run", "do not run any tests. (Useful for displaying information and exiting.)" );
		Supplier<Boolean> noCacheOption = clio.addSwitchOption( "--no-cache", "do not use cache." );
		Supplier<Boolean> noModuleOrder = clio.addSwitchOption( "--no-module-order", "do not order test modules by dependency." );
		Supplier<Boolean> noClassOrder = clio.addSwitchOption( "--no-class-order", "do not order test classes by dependency." );
		Supplier<Boolean> noMethodOrder = clio.addSwitchOption( "--no-method-order", "do not order test methods by source line number." );
		Supplier<Boolean> noAncestryOrder = clio.addSwitchOption( "--no-ancestry-order", "do not run test methods of ancestor classes first." );
		Supplier<Path> projectSourceRootDirectoryArgument = clio.addPathPositionalOption( "project-source-root-directory", ".", "the root source directory of the project." );
		Supplier<Path> settingsPathNameArgument = clio.addPathPositionalOption( "settings-pathname", "./testana.settings", "the pathname of the settings file." );
		Supplier<Path> persistencePathNameArgument = clio.addPathPositionalOption( "persistence-pathname", "./.testana.persistence.json", "the pathname of the persistence file." );
		Supplier<Path> cachePathNameArgument = clio.addPathPositionalOption( "cache-pathname", "./.testana.cache.json", "the pathname of the cache file." );
		if( !clio.parse( commandLineArguments ) )
			return -1;

		if( resetHistorySwitch.get() )
		{
			Persistence persistence = new Persistence( persistencePathNameArgument.get(), true, false );
			persistence.save();
			TestanaLog.report( "History cleared." );
		}
		else
		{
			for( ; ; )
			{
				Optional<Path> cachePathName = noCacheOption.get()? Optional.empty() : Optional.of( cachePathNameArgument.get() );
				run1( projectSourceRootDirectoryArgument.get(), settingsPathNameArgument.get(), persistencePathNameArgument.get(), cachePathName, //
					structureShowOption.get(), testPlanShowOption.get(), noRunOption.get(), doNotSaveHistorySwitch.get(), //
					noModuleOrder.get()? ModuleOrdering.None : ModuleOrdering.ByDependency, //
					noClassOrder.get()? ClassOrdering.None : ClassOrdering.ByDependency, //
					noMethodOrder.get()? MethodOrdering.None : MethodOrdering.ByNaturalOrder, //
					noAncestryOrder.get()? AncestryOrdering.None : AncestryOrdering.AncestorFirst );
				if( !(boolean)loopOption.get() )
					break;
			}
		}

		return 0;
	}

	private void run1( Path sourceDirectory, Path configurationPathName, Path persistencePathName, Optional<Path> cachePathName, //
		ProjectStructure.ShowOption structureShowOption, TestPlan.ShowOption testPlanShowOption, boolean noRun, boolean doNotSaveHistory, //
		ModuleOrdering moduleOrdering, ClassOrdering classOrdering, MethodOrdering methodOrdering, AncestryOrdering ancestryOrdering )
	{
		StructureSettings structureSettings = new StructureSettings();
		structureSettings.load( sourceDirectory, configurationPathName );

		Persistence persistence  = new Persistence( persistencePathName, false, true );

		Collection<TestEngine> testEngines = List.of( new JunitTestEngine( methodOrdering, ancestryOrdering ) );

		Collection<Discoverer> discoverers = List.of( new MavenDiscoverer() );

		TimeMeasurement.run( "Running testana", "done", timeMeasurement ->
		{
			ProjectStructure structure = ProjectStructureBuilder.build( sourceDirectory, discoverers, structureSettings, cachePathName, testEngines );
			structure.show( structureShowOption );

			TestPlan testPlan = TestPlanBuilder.build( persistence, structure, moduleOrdering, classOrdering );
			testPlan.show( testPlanShowOption );

			if( !noRun )
			{
				TestResult testResult = TestRunner.run( testPlan, persistence );

				TestanaLog.report( "-".repeat( 80 ) );
				TestanaLog.report( testResult.getOutcomeMessage() );
				boolean success = testResult.failureCount() == 0;
				String message = " " + (success? "Success (" + testResult.successCount() + "/" + testResult.totalCount() + ")" : "Failure") + " ";
				renderText( message );
				TestanaLog.report( "-".repeat( 80 ) );
			}
		} );

		if( persistence.isDirty() && !doNotSaveHistory )
			persistence.save();

		structureSettings.reportWarnings();
	}

	private static void renderText( String message )
	{
		renderText( message, new QuadrantTextFontRenderer( new BitmapFontRenderer( Tiny2Font.Instance, 2, 1 ) ) );
	}

	private static void renderText( String message, TextFontRenderer textFontRenderer )
	{
		for( String s : textFontRenderer.render( message ) )
			TestanaLog.report( s );
	}
}
