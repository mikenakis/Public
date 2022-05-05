package mikenakis.testana.test;

import mikenakis.kit.Kit;
import mikenakis.testana.AncestryOrdering;
import mikenakis.testana.ClassOrdering;
import mikenakis.testana.MethodOrdering;
import mikenakis.testana.ModuleOrdering;
import mikenakis.testana.Persistence;
import mikenakis.testana.StructureSettings;
import mikenakis.testana.TestEngine;
import mikenakis.testana.discovery.Discoverer;
import mikenakis.testana.discovery.maven.MavenDiscoverer;
import mikenakis.testana.runtime.TestRunner;
import mikenakis.testana.runtime.result.TestResult;
import mikenakis.testana.structure.ProjectModule;
import mikenakis.testana.structure.ProjectStructure;
import mikenakis.testana.structure.ProjectStructureBuilder;
import mikenakis.testana.structure.ProjectType;
import mikenakis.testana.structure.cache.Cache;
import mikenakis.testana.test_engines.junit.JunitTestEngine;
import mikenakis.testana.testplan.TestPlan;
import mikenakis.testana.testplan.TestPlanBuilder;
import org.junit.Test;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

/**
 * Testana Test.
 *
 * @author michael.gr
 */
public class T02_TestRunner
{
	private static final Instant T0 = Instant.parse( "2018-02-23T22:34:00.00Z" );

	public T02_TestRunner()
	{
		if( !Kit.areAssertionsEnabled() )
			throw new AssertionError();
	}

	@Test public void test_resource_file_is_accessible()
	{
		URL url = getClass().getResource( "/sample_test_resource_file.txt" );
		assert url != null;
		Path path = Kit.classLoading.getPathFromUrl( url );
		List<String> lines = Kit.unchecked( () -> Files.readAllLines(path) );
		assert lines.size() == 1;
		assert lines.get( 0 ).equals( "sample test resource content" );
	}

	@Test public void test_resource_directory_does_not_get_mixed_with_testana_resource_directory()
	{
		URL url = getClass().getClassLoader().getResource( "" );
		assert url != null;
		assert url.equals( getClass().getResource( "/" ) );
		assert url.equals( getClass().getResource( "/." ) );
		Path path = Kit.classLoading.getPathFromUrl( url );
		assert !path.toString().contains( "testana-console" ); //Note: this used to fail, then it was fixed, but we keep it as a regression test.
	}

	@Test public void full_run_is_full()
	{
		Persistence persistence = new Persistence( null, true, false );
		ProjectStructure structure = createStructure( MethodOrdering.None, AncestryOrdering.None );
		TestPlan testPlan = TestPlanBuilder.build( persistence, structure, ModuleOrdering.None, ClassOrdering.None );
		TestResult testResult = TestRunner.run( testPlan, persistence );
		assert testResult.ignoredCount() == 0;
		assert testResult.failureCount() == 0;
		assert testResult.successCount() == 4;
		assert testResult.totalCount() == 4;
		assert testResult.upToDateCount() == 0;
	}

	private static ProjectStructure createStructure( MethodOrdering methodOrdering, AncestryOrdering ancestryOrdering )
	{
		Path sourceDirectory = Kit.path.getWorkingDirectory().resolve( "../testana-test-rigs" ).normalize();
		Collection<Discoverer> discoverers = List.of( new MavenDiscoverer() );
		StructureSettings settings = new StructureSettings();
		Iterable<TestEngine> testEngines = List.of( new JunitTestEngine( methodOrdering, ancestryOrdering ) );
		ProjectStructure structure = ProjectStructureBuilder.build( sourceDirectory, discoverers, settings, Cache.empty(), testEngines );
		for( ProjectModule projectModule : structure.projectModules() )
			for( ProjectType projectType : projectModule.getProjectTypes() )
				projectType.setLastModifiedTime( T0 );
		return structure;
	}
}
