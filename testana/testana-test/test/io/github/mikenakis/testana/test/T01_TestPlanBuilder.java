package io.github.mikenakis.testana.test;

import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.kit.logging.Log;
import io.github.mikenakis.testana.AncestryOrdering;
import io.github.mikenakis.testana.ClassOrdering;
import io.github.mikenakis.testana.MethodOrdering;
import io.github.mikenakis.testana.ModuleOrdering;
import io.github.mikenakis.testana.Persistence;
import io.github.mikenakis.testana.StructureSettings;
import io.github.mikenakis.testana.TestEngine;
import io.github.mikenakis.testana.discovery.Discoverer;
import io.github.mikenakis.testana.discovery.maven.MavenDiscoverer;
import io.github.mikenakis.testana.structure.ProjectModule;
import io.github.mikenakis.testana.structure.ProjectStructure;
import io.github.mikenakis.testana.structure.ProjectStructureBuilder;
import io.github.mikenakis.testana.structure.ProjectType;
import io.github.mikenakis.testana.structure.cache.Cache;
import io.github.mikenakis.testana.test.rigs.classes_under_test.Alice;
import io.github.mikenakis.testana.test.rigs.classes_under_test.Claire;
import io.github.mikenakis.testana.test.rigs.rig2.T01_Test;
import io.github.mikenakis.testana.test.rigs.rig2.T02_Test;
import io.github.mikenakis.testana.test.rigs.rig3.T03_Test;
import io.github.mikenakis.testana.test_engines.junit.JunitTestEngine;
import io.github.mikenakis.testana.testplan.TestPlan;
import io.github.mikenakis.testana.testplan.TestPlanBuilder;
import io.github.mikenakis.testana.testplan.intent.Intent;
import io.github.mikenakis.testana.testplan.intent.NoRunBecauseUpToDateIntent;
import io.github.mikenakis.testana.testplan.intent.RunBecauseDependenciesModifiedIntent;
import io.github.mikenakis.testana.testplan.intent.RunBecauseModifiedSinceLastRunIntent;
import io.github.mikenakis.testana.testplan.intent.RunBecauseNeverRunBeforeIntent;
import org.junit.Test;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Testana Test.
 *
 * @author michael.gr
 */
public class T01_TestPlanBuilder
{
	private static class Node
	{
		final String name;
		final Intent intention;

		Node( String name, Intent intention )
		{
			this.name = name;
			this.intention = intention;
		}

		@Override public boolean equals( Object other )
		{
			if( other instanceof Node )
				return equals( (Node)other );
			assert false;
			return false;
		}

		@Override public int hashCode()
		{
			return Objects.hash( name, intention );
		}

		public boolean equals( Node other )
		{
			if( !name.equals( other.name ) )
				return false;
			if( !intention.equals( other.intention ) )
				return false;
			return true;
		}

		@Override public String toString()
		{
			return name + ": " + intention.getClass().getSimpleName() + ": " + intention;
		}
	}

	private static final Instant T0 = Instant.parse( "2018-02-23T22:34:00.00Z" );
	private static final Instant T1 = T0.plus( Duration.ofSeconds( 1 ) );
	private static final Instant T2 = T1.plus( Duration.ofSeconds( 1 ) );

	public T01_TestPlanBuilder()
	{
		if( !Kit.areAssertionsEnabled() )
			throw new AssertionError();
	}

	@Test public void Normal_Scenario_Runs_Nothing()
	{
		Persistence persistence = new Persistence( null, true, false );
		persistence.setTimeOfLastRun( T01_Test.class.getName(), T1 );
		persistence.setTimeOfLastRun( T02_Test.class.getName(), T1 );
		persistence.setTimeOfLastRun( T03_Test.class.getName(), T1 );
		ProjectStructure structure = createStructure( MethodOrdering.None, AncestryOrdering.None );
		TestPlan testPlan = TestPlanBuilder.build( persistence, structure, ModuleOrdering.None, ClassOrdering.None );

		List<Node> nodes = collectNodes( testPlan );
		assert nodes.equals( List.of( new Node( T01_Test.class.getName(), NoRunBecauseUpToDateIntent.INSTANCE ), //
			new Node( T02_Test.class.getName(), NoRunBecauseUpToDateIntent.INSTANCE ), //
			new Node( T03_Test.class.getName(), NoRunBecauseUpToDateIntent.INSTANCE ) ) );
	}

	@Test public void Empty_Persistence_Runs_Everything()
	{
		Persistence persistence = new Persistence( null, true, false );
		ProjectStructure structure = createStructure( MethodOrdering.None, AncestryOrdering.None );
		TestPlan testPlan = TestPlanBuilder.build( persistence, structure, ModuleOrdering.None, ClassOrdering.None );

		List<Node> nodes = collectNodes( testPlan );
		assert nodes.equals( List.of( new Node( T01_Test.class.getName(), RunBecauseNeverRunBeforeIntent.INSTANCE ), //
			new Node( T02_Test.class.getName(), RunBecauseNeverRunBeforeIntent.INSTANCE ), //
			new Node( T03_Test.class.getName(), RunBecauseNeverRunBeforeIntent.INSTANCE ) ) );
	}

	@Test public void Modified_Test_Class_is_Detected()
	{
		Persistence persistence = new Persistence( null, true, false );
		persistence.setTimeOfLastRun( T01_Test.class.getName(), T1 );
		persistence.setTimeOfLastRun( T02_Test.class.getName(), T1 );
		persistence.setTimeOfLastRun( T03_Test.class.getName(), T1 );
		ProjectStructure structure = createStructure( MethodOrdering.None, AncestryOrdering.None );
		setLastModifiedTime( structure, T01_Test.class, T2 );
		setLastModifiedTime( structure, T03_Test.class, T2 );
		TestPlan testPlan = TestPlanBuilder.build( persistence, structure, ModuleOrdering.None, ClassOrdering.None );

		List<Node> nodes = collectNodes( testPlan );
		assert nodes.equals( List.of( new Node( T01_Test.class.getName(), new RunBecauseModifiedSinceLastRunIntent( T1, T2 ) ), //
			new Node( T02_Test.class.getName(), NoRunBecauseUpToDateIntent.INSTANCE ), //
			new Node( T03_Test.class.getName(), new RunBecauseModifiedSinceLastRunIntent( T1, T2 ) ) ) );
	}

	@Test public void Modified_Immediate_Dependency_is_Detected()
	{
		Persistence persistence = new Persistence( null, true, false );
		persistence.setTimeOfLastRun( T01_Test.class.getName(), T1 );
		persistence.setTimeOfLastRun( T02_Test.class.getName(), T1 );
		persistence.setTimeOfLastRun( T03_Test.class.getName(), T1 );
		ProjectStructure structure = createStructure( MethodOrdering.None, AncestryOrdering.None );
		setLastModifiedTime( structure, T01_Test.class, T1 );
		setLastModifiedTime( structure, T02_Test.class, T1 );
		setLastModifiedTime( structure, T03_Test.class, T1 );
		setLastModifiedTime( structure, Claire.class, T2 );
		TestPlan testPlan = TestPlanBuilder.build( persistence, structure, ModuleOrdering.None, ClassOrdering.None );

		List<Node> nodes = collectNodes( testPlan );
		assert nodes.equals( List.of( new Node( T01_Test.class.getName(), //
				new RunBecauseDependenciesModifiedIntent( T1, List.of( new RunBecauseDependenciesModifiedIntent.Entry( Claire.class.getName(), T2 ) ) ) ), //
			new Node( T02_Test.class.getName(), NoRunBecauseUpToDateIntent.INSTANCE ), //
			new Node( T03_Test.class.getName(), NoRunBecauseUpToDateIntent.INSTANCE ) ) );
	}

	@Test public void Modified_Transitive_Dependency_is_Detected()
	{
		Persistence persistence = new Persistence( null, true, false );
		persistence.setTimeOfLastRun( T01_Test.class.getName(), T1 );
		persistence.setTimeOfLastRun( T02_Test.class.getName(), T2 );
		persistence.setTimeOfLastRun( T03_Test.class.getName(), T2 );
		ProjectStructure structure = createStructure( MethodOrdering.None, AncestryOrdering.None );
		setLastModifiedTime( structure, Alice.class, T2 );
		TestPlan testPlan = TestPlanBuilder.build( persistence, structure, ModuleOrdering.None, ClassOrdering.None );

		List<Node> nodes = collectNodes( testPlan );
		assert nodes.equals( List.of( new Node( T01_Test.class.getName(), //
				new RunBecauseDependenciesModifiedIntent( T1, List.of( new RunBecauseDependenciesModifiedIntent.Entry( Alice.class.getName(), T2 ) ) ) ), //
			new Node( T02_Test.class.getName(), NoRunBecauseUpToDateIntent.INSTANCE ), //
			new Node( T03_Test.class.getName(), NoRunBecauseUpToDateIntent.INSTANCE ) ) );
	}

	@Test public void Order_of_Test_Class_Execution_is_Alphabetic_When_Sorting_Disabled()
	{
		Persistence persistence = new Persistence( null, true, false );
		persistence.setTimeOfLastRun( T01_Test.class.getName(), T1 );
		persistence.setTimeOfLastRun( T02_Test.class.getName(), T1 );
		persistence.setTimeOfLastRun( T03_Test.class.getName(), T1 );
		ProjectStructure structure = createStructure( MethodOrdering.None, AncestryOrdering.None );
		setLastModifiedTime( structure, Alice.class, T2 );
		TestPlan testPlan = TestPlanBuilder.build( persistence, structure, ModuleOrdering.None, ClassOrdering.None );

		List<Node> nodes = collectNodes( testPlan );
		assert nodes.equals( List.of( new Node( T01_Test.class.getName(), //
				new RunBecauseDependenciesModifiedIntent( T1, List.of( new RunBecauseDependenciesModifiedIntent.Entry( Alice.class.getName(), T2 ) ) ) ), //
			new Node( T02_Test.class.getName(), new RunBecauseDependenciesModifiedIntent( T1, List.of( new RunBecauseDependenciesModifiedIntent.Entry( Alice.class.getName(), T2 ) ) ) ), //
			new Node( T03_Test.class.getName(), NoRunBecauseUpToDateIntent.INSTANCE ) ) );
	}

	@Test public void Order_of_Test_Class_Execution_is_by_Dependency_When_Sorting_Enabled()
	{
		Persistence persistence = new Persistence( null, true, false );
		persistence.setTimeOfLastRun( T01_Test.class.getName(), T1 );
		persistence.setTimeOfLastRun( T02_Test.class.getName(), T1 );
		persistence.setTimeOfLastRun( T03_Test.class.getName(), T1 );
		ProjectStructure structure = createStructure( MethodOrdering.None, AncestryOrdering.None );
		setLastModifiedTime( structure, Alice.class, T2 );
		TestPlan testPlan = TestPlanBuilder.build( persistence, structure, ModuleOrdering.None, ClassOrdering.ByDependency );

		List<Node> nodes = collectNodes( testPlan );
		assert nodes.equals( List.of( //
			new Node( T02_Test.class.getName(), new RunBecauseDependenciesModifiedIntent( T1, List.of( new RunBecauseDependenciesModifiedIntent.Entry( Alice.class.getName(), T2 ) ) ) ), //
			new Node( T01_Test.class.getName(), new RunBecauseDependenciesModifiedIntent( T1, List.of( new RunBecauseDependenciesModifiedIntent.Entry( Alice.class.getName(), T2 ) ) ) ), //
			new Node( T03_Test.class.getName(), NoRunBecauseUpToDateIntent.INSTANCE ) ) );
	}

	@Test public void Order_of_Test_Method_Execution_is_Alphabetic_By_Default()
	{
		Persistence persistence = new Persistence( null, true, false );
		ProjectStructure structure = createStructure( MethodOrdering.None, AncestryOrdering.None );
		TestPlan testPlan = TestPlanBuilder.build( persistence, structure, ModuleOrdering.None, ClassOrdering.None );

		List<String> names = collectMethodNames( testPlan );
		assert names.equals( List.of( "T01_Test.ClaireTest()", "T01_Test.ZAliceTest()", "T02_Test.ZAliceTest()", "T03_Test.test()" ) );
	}

	@Test public void Order_of_Test_Method_Execution_is_by_Inheritance_When_Run_Ancestors_First_Is_Enabled()
	{
		Persistence persistence = new Persistence( null, true, false );
		ProjectStructure structure = createStructure( MethodOrdering.None, AncestryOrdering.AncestorFirst );
		TestPlan testPlan = TestPlanBuilder.build( persistence, structure, ModuleOrdering.None, ClassOrdering.None );

		List<String> names = collectMethodNames( testPlan );
		assert names.equals( List.of( "T01_Test.ZAliceTest()", "T01_Test.ClaireTest()", "T02_Test.ZAliceTest()", "T03_Test.test()" ) );
	}

	private static void setLastModifiedTime( ProjectStructure projectStructure, Class<?> javaClass, Instant lastModifiedTime )
	{
		ProjectType projectType = getProjectTypeByName( projectStructure, javaClass.getName() );
		projectType.setLastModifiedTime( lastModifiedTime );
	}

	private static ProjectType getProjectTypeByName( ProjectStructure projectStructure, String typeName )
	{
		for( ProjectModule projectModule : projectStructure.projectModules() )
		{
			Optional<ProjectType> projectType = projectModule.tryGetProjectTypeByName( typeName );
			if( projectType.isPresent() )
				return projectType.get();
		}
		throw new AssertionError();
	}

	private static List<String> collectMethodNames( TestPlan testPlan )
	{
		return testPlan.testModules().stream() //
			.flatMap( testModule -> testModule.testClasses().stream() ) //
			.flatMap( testClass -> testClass.testMethods().stream().map( m -> testClass.simpleName() + "." + m.name() ) ) //
			.toList();
	}

	private static List<Node> collectNodes( TestPlan testPlan )
	{
		return testPlan.testModules().stream() //
			.flatMap( testModule -> testModule.testClasses().stream() ) //
			.map( entry -> new Node( entry.fullName(), testPlan.getIntent( entry ) ) ) //
			.collect( Collectors.toList() );
	}

	private static ProjectStructure createStructure( MethodOrdering methodOrdering, AncestryOrdering ancestryOrdering )
	{
		Path userDir = Kit.path.getWorkingDirectory();
		Log.debug( "UserDir = " + userDir );
		Path sourceDirectory = userDir.resolve( "../testana-test-rigs" ).normalize();
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
