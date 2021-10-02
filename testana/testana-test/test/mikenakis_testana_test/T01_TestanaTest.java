package mikenakis_testana_test;

import mikenakis.kit.Kit;
import mikenakis.kit.logging.Log;
import mikenakis.testana.AncestryOrdering;
import mikenakis.testana.ClassOrdering;
import mikenakis.testana.MethodOrdering;
import mikenakis.testana.ModuleOrdering;
import mikenakis.testana.Persistence;
import mikenakis.testana.StructureSettings;
import mikenakis.testana.TestEngine;
import mikenakis.testana.discovery.Discoverer;
import mikenakis.testana.discovery.maven.MavenDiscoverer;
import mikenakis.testana.structure.ProjectModule;
import mikenakis.testana.structure.ProjectStructure;
import mikenakis.testana.structure.ProjectStructureBuilder;
import mikenakis.testana.structure.ProjectType;
import mikenakis.testana.structure.cache.Cache;
import mikenakis.testana.test.rig1.Alice;
import mikenakis.testana.test.rig1.Claire;
import mikenakis.testana.test.rig2.T01_ClaireTest;
import mikenakis.testana.test.rig2.T02_AliceTest;
import mikenakis.testana.test_engines.junit.JunitTestEngine;
import mikenakis.testana.testplan.TestPlan;
import mikenakis.testana.testplan.TestPlanBuilder;
import mikenakis.testana.testplan.intent.FirstRunIntent;
import mikenakis.testana.testplan.intent.Intent;
import mikenakis.testana.testplan.intent.ModifiedRunIntent;
import mikenakis.testana.testplan.intent.UpToDateIntent;
import mikenakis.testana.testplan.intent.UpdateRunIntent;
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
 * @author Michael Belivanakis (michael.gr)
 */
public class T01_TestanaTest
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

	public T01_TestanaTest()
	{
	}

	@Test public void Normal_Scenario_Runs_Nothing()
	{
		Persistence persistence = new Persistence( null, true, false );
		persistence.setTimeOfLastRun( T01_ClaireTest.class.getName(), T1 );
		persistence.setTimeOfLastRun( T02_AliceTest.class.getName(), T1 );
		ProjectStructure structure = createStructure( MethodOrdering.None, AncestryOrdering.None );
		TestPlan testPlan = TestPlanBuilder.build( persistence, structure, ModuleOrdering.None, ClassOrdering.None );

		List<Node> nodes = collectNodes( testPlan );
		assert nodes.equals( List.of( new Node( T01_ClaireTest.class.getName(), UpToDateIntent.INSTANCE ), //
			new Node( T02_AliceTest.class.getName(), UpToDateIntent.INSTANCE ) ) );
	}

	@Test public void Empty_Persistence_Runs_Everything()
	{
		Persistence persistence = new Persistence( null, true, false );
		ProjectStructure structure = createStructure( MethodOrdering.None, AncestryOrdering.None );
		TestPlan testPlan = TestPlanBuilder.build( persistence, structure, ModuleOrdering.None, ClassOrdering.None );

		List<Node> nodes = collectNodes( testPlan );
		assert nodes.equals( List.of( new Node( T01_ClaireTest.class.getName(), FirstRunIntent.INSTANCE ), //
			new Node( T02_AliceTest.class.getName(), FirstRunIntent.INSTANCE ) ) );
	}

	@Test public void Modified_Test_Class_is_Detected()
	{
		Persistence persistence = new Persistence( null, true, false );
		persistence.setTimeOfLastRun( T01_ClaireTest.class.getName(), T1 );
		persistence.setTimeOfLastRun( T02_AliceTest.class.getName(), T1 );
		ProjectStructure structure = createStructure( MethodOrdering.None, AncestryOrdering.None );
		setLastModifiedTime( structure, T01_ClaireTest.class, T2 );
		TestPlan testPlan = TestPlanBuilder.build( persistence, structure, ModuleOrdering.None, ClassOrdering.None );

		List<Node> nodes = collectNodes( testPlan );
		assert nodes.equals( List.of( new Node( T01_ClaireTest.class.getName(), new ModifiedRunIntent( T1, T2 ) ), //
			new Node( T02_AliceTest.class.getName(), UpToDateIntent.INSTANCE ) ) );
	}

	@Test public void Modified_Immediate_Dependency_is_Detected()
	{
		Persistence persistence = new Persistence( null, true, false );
		persistence.setTimeOfLastRun( T01_ClaireTest.class.getName(), T1 );
		persistence.setTimeOfLastRun( T02_AliceTest.class.getName(), T1 );
		ProjectStructure structure = createStructure( MethodOrdering.None, AncestryOrdering.None );
		setLastModifiedTime( structure, T01_ClaireTest.class, T1 );
		setLastModifiedTime( structure, T02_AliceTest.class, T1 );
		setLastModifiedTime( structure, Claire.class, T2 );
		TestPlan testPlan = TestPlanBuilder.build( persistence, structure, ModuleOrdering.None, ClassOrdering.None );

		List<Node> nodes = collectNodes( testPlan );
		assert nodes.equals( List.of( new Node( T01_ClaireTest.class.getName(), //
				new UpdateRunIntent( T1, List.of( new UpdateRunIntent.Entry( Claire.class.getName(), T2 ) ) ) ), //
			new Node( T02_AliceTest.class.getName(), UpToDateIntent.INSTANCE ) ) );
	}

	@Test public void Modified_Transitive_Dependency_is_Detected()
	{
		Persistence persistence = new Persistence( null, true, false );
		persistence.setTimeOfLastRun( T01_ClaireTest.class.getName(), T1 );
		persistence.setTimeOfLastRun( T02_AliceTest.class.getName(), T2 );
		ProjectStructure structure = createStructure( MethodOrdering.None, AncestryOrdering.None );
		setLastModifiedTime( structure, Alice.class, T2 );
		TestPlan testPlan = TestPlanBuilder.build( persistence, structure, ModuleOrdering.None, ClassOrdering.None );

		List<Node> nodes = collectNodes( testPlan );
		assert nodes.equals( List.of( new Node( T01_ClaireTest.class.getName(), //
				new UpdateRunIntent( T1, List.of( new UpdateRunIntent.Entry( Alice.class.getName(), T2 ) ) ) ), //
			new Node( T02_AliceTest.class.getName(), UpToDateIntent.INSTANCE ) ) );
	}

	@Test public void Order_of_Test_Class_Execution_is_Alphabetic_When_Sorting_Disabled()
	{
		Persistence persistence = new Persistence( null, true, false );
		persistence.setTimeOfLastRun( T01_ClaireTest.class.getName(), T1 );
		persistence.setTimeOfLastRun( T02_AliceTest.class.getName(), T1 );
		ProjectStructure structure = createStructure( MethodOrdering.None, AncestryOrdering.None );
		setLastModifiedTime( structure, Alice.class, T2 );
		TestPlan testPlan = TestPlanBuilder.build( persistence, structure, ModuleOrdering.None, ClassOrdering.None );

		List<Node> nodes = collectNodes( testPlan );
		assert nodes.equals( List.of( new Node( T01_ClaireTest.class.getName(), //
				new UpdateRunIntent( T1, List.of( new UpdateRunIntent.Entry( Alice.class.getName(), T2 ) ) ) ), //
			new Node( T02_AliceTest.class.getName(), new UpdateRunIntent( T1, List.of( new UpdateRunIntent.Entry( Alice.class.getName(), T2 ) ) ) ) ) );
	}

	@Test public void Order_of_Test_Class_Execution_is_by_Dependency_When_Sorting_Enabled()
	{
		Persistence persistence = new Persistence( null, true, false );
		persistence.setTimeOfLastRun( T01_ClaireTest.class.getName(), T1 );
		persistence.setTimeOfLastRun( T02_AliceTest.class.getName(), T1 );
		ProjectStructure structure = createStructure( MethodOrdering.None, AncestryOrdering.None );
		setLastModifiedTime( structure, Alice.class, T2 );
		TestPlan testPlan = TestPlanBuilder.build( persistence, structure, ModuleOrdering.None, ClassOrdering.ByDependency );

		List<Node> nodes = collectNodes( testPlan );
		assert nodes.equals( List.of( //
			new Node( T02_AliceTest.class.getName(), new UpdateRunIntent( T1, List.of( new UpdateRunIntent.Entry( Alice.class.getName(), T2 ) ) ) ), //
			new Node( T01_ClaireTest.class.getName(), new UpdateRunIntent( T1, List.of( new UpdateRunIntent.Entry( Alice.class.getName(), T2 ) ) ) ) ) );
	}

	@Test public void Order_of_Test_Method_Execution_is_Alphabetic_By_Default()
	{
		Persistence persistence = new Persistence( null, true, false );
		ProjectStructure structure = createStructure( MethodOrdering.None, AncestryOrdering.None );
		TestPlan testPlan = TestPlanBuilder.build( persistence, structure, ModuleOrdering.None, ClassOrdering.None );

		List<String> names = collectMethodNames( testPlan );
		assert names.equals( List.of( "T01_ClaireTest.ClaireTest()", "T01_ClaireTest.ZAliceTest()", "T02_AliceTest.ZAliceTest()" ) );
	}

	@Test public void Order_of_Test_Method_Execution_is_by_Inheritance_When_Run_Ancestors_First_Is_Enabled()
	{
		Persistence persistence = new Persistence( null, true, false );
		ProjectStructure structure = createStructure( MethodOrdering.None, AncestryOrdering.AncestorFirst );
		TestPlan testPlan = TestPlanBuilder.build( persistence, structure, ModuleOrdering.None, ClassOrdering.None );

		List<String> names = collectMethodNames( testPlan );
		assert names.equals( List.of( "T01_ClaireTest.ZAliceTest()", "T01_ClaireTest.ClaireTest()", "T02_AliceTest.ZAliceTest()" ) );
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
		Path sourceDirectory = getProjectDirectory().resolve( "../testana-test-rigs" ).normalize();
		Collection<Discoverer> discoverers = List.of( new MavenDiscoverer() );
		StructureSettings settings = new StructureSettings();
		Iterable<TestEngine> testEngines = List.of( new JunitTestEngine( methodOrdering, ancestryOrdering ) );
		ProjectStructure structure = ProjectStructureBuilder.build( sourceDirectory, discoverers, settings, Cache.empty(), testEngines );
		for( ProjectModule projectModule : structure.projectModules() )
			for( ProjectType projectType : projectModule.getProjectTypes() )
				projectType.setLastModifiedTime( T0 );
		return structure;
	}

	private static Path getProjectDirectory()
	{
		Path path = Kit.path.getWorkingDirectory();
		Log.debug( "Working directory: " + path );
		assert path.resolve( "test/mikenakis_testana_test/T01_TestanaTest.java" ).toFile().exists(); //Current directory is not ${project.basedir}
		return path;
	}
}
