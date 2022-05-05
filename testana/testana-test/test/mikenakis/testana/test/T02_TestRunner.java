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

	@Test public void Full_Run_Causes_All_Methods_To_Be_Invoked()
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
