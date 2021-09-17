package mikenakis.testana.testplan;

import mikenakis.kit.Kit;
import mikenakis.kit.logging.Log;
import mikenakis.testana.ClassOrdering;
import mikenakis.testana.ModuleOrdering;
import mikenakis.testana.Persistence;
import mikenakis.testana.kit.TimeMeasurement;
import mikenakis.testana.structure.ProjectModule;
import mikenakis.testana.structure.ProjectStructure;
import mikenakis.testana.structure.ProjectType;
import mikenakis.testana.testplan.dependency.DependencyMatrix;
import mikenakis.testana.testplan.intent.FirstRunIntent;
import mikenakis.testana.testplan.intent.Intent;
import mikenakis.testana.testplan.intent.ModifiedRunIntent;
import mikenakis.testana.testplan.intent.UpToDateIntent;
import mikenakis.testana.testplan.intent.UpdateRunIntent;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public final class TestPlanBuilder
{
	public static TestPlan build( Persistence persistence, ProjectStructure projectStructure, ModuleOrdering moduleOrdering, ClassOrdering classOrdering )
	{
		Collection<TestModule> testModules = collectTestModules( projectStructure );
		Map<TestClass,Intent> testClassIntents = calculateTestClassIntents( testModules, persistence );

		testModules = switch( moduleOrdering )
			{
				case None -> testModules;
				case ByDependency -> orderModules( testModules );
			};

		switch( classOrdering )
		{
			case None:
				break;
			case ByDependency:
				orderTestClasses( testModules );
				break;
			default:
				assert false;
		}

		return new TestPlan( testModules, testClassIntents );
	}

	private static Collection<TestModule> orderModules( Collection<TestModule> unorderedTestModules )
	{
		Log.warning( "TODO: implement module ordering! (the one currently in place does not work.)" );
		return TimeMeasurement.run( "Ordering test modules by dependency", "done", timeMeasurement -> //
		{
			//List<TestModule> testModules1 = new ArrayList<>( unorderedTestModules );
			//randomize( testModules1 ); //XXX
			//List<TestModule> testModules = new ArrayList<>( testModules1 );
			List<TestModule> testModules = new ArrayList<>( unorderedTestModules );
			Collection<ProjectModule> projectModules = getProjectModules( testModules );
			DependencyMatrix<ProjectModule> dependencyMatrix = new DependencyMatrix<>( projectModules, m -> m.allProjectDependencies() );
			Kit.sort.quickSort( testModules, ( a, b ) -> compare( a, b, dependencyMatrix ) );
			return testModules;
		} );
	}

	private static Collection<ProjectModule> getProjectModules( Collection<TestModule> testModules )
	{
		return testModules.stream().map( m -> m.projectModule() ).collect( Collectors.toList() );
	}

	private static <X> void randomize( List<X> list )
	{
		Random random = new Random( 1 );
		for( int i = 0; i < list.size(); i++ )
		{
			int j = random.nextInt( list.size() );
			Kit.list.swap( list, i, j );
		}
	}

	/**
	 * How to calculate indirect dependency
	 * <p>
	 * A depends on:
	 * I  (an irrelevant module that only A depends on.)
	 * J  (an irrelevant module that both A and B depend on.)
	 * L  (a library module)
	 * <p>
	 * B depends on:
	 * J  (an irrelevant module that both A and B depend on.)
	 * K  (an irrelevant module that only B depends on.)
	 * M  (a middleware module)
	 * <p>
	 * M depends on:
	 * L  (the library module)
	 * <p>
	 * Therefore, B is said to indirectly depend on A.
	 * <p>
	 * Dependency matrix: (modules along Y depend on modules along X)
	 * <p>
	 * +-----+-----+-----+-----+-----+-----+-----
	 * A  |     |     | 1 a |     | 1   | 1 c |
	 * ----+-----+-----+-----+-----+-----+-----+-----
	 * B  |     |     | 2 b | 1   |     | 1 d | 1
	 * ----+-----+-----+-----+-----+-----+-----+-----
	 * | A   | B   | L   | M   | I   | J   | K
	 * <p>
	 * The points of interest are those denoted with a, b, c, and d.
	 * The common dependencies between A and B are L and J.
	 * For J, both c and d are equal, so they do not determine the order of dependency.
	 * For L, b is higher than a, so it determines the order of dependency.
	 */
	private static int compare( TestModule a, TestModule b, DependencyMatrix<ProjectModule> dependencyMatrix )
	{
		//		if( a.name().equals( "mikenakis:saganaki-essentia-reflector-test" ) && b.name().equals( "mikenakis:saganaki-essentia-core-test" ) )
		//			Kit.get( true );
		//		if( a.name().equals( "mikenakis:saganaki-core-test" ) && b.name().equals( "mikenakis:saganaki-markup-json-test" ) )
		//			Kit.get( true );
		if( a == b )
			return 0;

		if( a.projectModule().dependsOn( b.projectModule() ) ) //extremely rare but very quick case: one test module depending on another.
			return 1;
		//		Collection<ProjectModule> dependenciesA = a.projectModule().projectDependencies();
		Collection<ProjectModule> allDependenciesA = a.projectModule().allProjectDependencies();
		//		Collection<ProjectModule> dependenciesB = b.projectModule().projectDependencies();
		Collection<ProjectModule> allDependenciesB = b.projectModule().allProjectDependencies();
		Collection<ProjectModule> commonDependencies = Kit.collection.intersection( allDependenciesA, allDependenciesB );
		int totalDistanceA = 1;
		int totalDistanceB = 1;
		for( ProjectModule commonDependency : commonDependencies )
		{
			int distanceA = dependencyMatrix.getDistance( a.projectModule(), commonDependency ).orElseThrow();
			int distanceB = dependencyMatrix.getDistance( b.projectModule(), commonDependency ).orElseThrow();
			if( distanceA == distanceB )
				continue;
			totalDistanceA *= distanceA;
			totalDistanceB *= distanceB;
		}
		return Integer.compare( totalDistanceA, totalDistanceB );
		//
		//		Collection<ProjectModule> leanDependenciesA = Kit.difference( dependenciesA, dependenciesB );
		//		Collection<ProjectModule> leanDependenciesB = Kit.difference( dependenciesB, dependenciesA );
		//		assert !Kit.containsAny( leanDependenciesA, leanDependenciesB );
		//		assert !Kit.containsAny( leanDependenciesB, leanDependenciesA );
		//		//boolean a = anyDependOnAny( leanDependencies, leanOtherDependencies );
		//		//boolean b = anyDependOnAny( leanOtherDependencies, leanDependencies );
		//		boolean a = anyDependOnAny( leanDependenciesA, dependenciesB );
		//		boolean b = anyDependOnAny( leanDependenciesB, dependenciesA );
		//		//assert !a || !b;
		//		return a ? 1 : b ? -1 : 0;
		//		for( ProjectModule otherImmediateDependency : otherTestModule.projectModule.projectDependencies() )
		//			if( projectModule.dependsOn( otherImmediateDependency ) )
		//				return true;
		//		return false;
	}

	private static boolean anyDependOnAny( Collection<ProjectModule> a, Collection<ProjectModule> b )
	{
		for( ProjectModule projectModule : a )
			if( dependsOnAny( projectModule, b ) )
				return true;
		return false;
	}

	private static boolean dependsOnAny( ProjectModule a, Collection<ProjectModule> b )
	{
		for( ProjectModule projectModule : b )
			if( a.dependsOn( projectModule ) )
				return true;
		return false;
	}

	//	private static boolean reorderOneModule( List<TestModule> testModules )
	//	{
	//		for( int i = 0; i < testModules.size(); i++ )
	//		{
	//			TestModule testModule = testModules.get( i );
	//			for( int j = i + 1; j < testModules.size(); j++ )
	//			{
	//				TestModule otherTestModule = testModules.get( j );
	//				if( testModule.dependsOn( otherTestModule ) )
	//				{
	//					Kit.swap( testModules, i, j );
	//					return true;
	//				}
	//			}
	//		}
	//		return false;
	//	}

	private static void orderTestClasses( Collection<TestModule> testModules )
	{
		TimeMeasurement.run( "Ordering test classes by dependency", "Ordered %d test classes in %d test modules", timeMeasurement -> //
		{
			int classCount = 0;
			for( TestModule testModule : testModules )
			{
				Map<TestClass,Map<ProjectType,Integer>> dependencyDistanceGraph = createDependencyDistanceGraph( testModule.testClasses() );
				testModule.sortTestClasses( ( a, b ) -> compareTestClasses( a, b, dependencyDistanceGraph ) );
				classCount += testModule.testClasses().size();
			}
			timeMeasurement.setArguments( classCount, testModules.size() );
		} );
	}

	private static Collection<TestModule> collectTestModules( ProjectStructure projectStructure )
	{
		Log.warning( "TODO: do not involve the test engines at this stage; involve them only when running the tests, and only for the tests that need to run." );
		return TimeMeasurement.run( "Collecting test modules, classes, and methods", "Collected %d test modules, %d test classes", timeMeasurement -> {
			Collection<TestModule> testModules = new LinkedHashSet<>();
			for( ProjectModule projectModule : projectStructure.projectModules() )
			{
				List<TestClass> testClasses = new ArrayList<>();
				for( ProjectType projectType : projectModule.getProjectTypes() )
				{
					projectType.testEngine().ifPresent( testEngine -> //
					{
						TestClass testClass = testEngine.createTestClass( projectType );
						testClasses.add( testClass );
					} );
				}
				if( testClasses.isEmpty() )
					continue;
				TestModule testModule = new TestModule( projectModule, testClasses );
				Kit.collection.add( testModules, testModule );
			}
			timeMeasurement.setArguments( testModules.size(), testModules.stream().map( m -> m.testClasses().size() ).reduce( 0, ( a, b ) -> a + b ) );
			return testModules;
		} );
	}

	private static Map<TestClass,Intent> calculateTestClassIntents( Iterable<TestModule> testModules, Persistence persistence )
	{
		return TimeMeasurement.run( "Calculating intentions", "Calculated intentions", timeMeasurement -> //
		{
			Map<TestClass,Intent> testClassIntents = new LinkedHashMap<>();
			for( TestModule testModule : testModules )
				for( TestClass testClass : testModule.testClasses() )
				{
					Intent intention = calculateIntention( testClass.projectType, persistence );
					Kit.map.add( testClassIntents, testClass, intention );
				}
			return testClassIntents;
		} );
	}

	private static Intent calculateIntention( ProjectType projectType, Persistence persistence )
	{
		Optional<Instant> timeOfLastRun = persistence.tryGetTimeOfLastRun( projectType.className() );
		if( timeOfLastRun.isEmpty() )
			return FirstRunIntent.INSTANCE;
		if( projectType.getLastModifiedTime().isAfter( timeOfLastRun.get() ) )
			return new ModifiedRunIntent( timeOfLastRun.get(), projectType.getLastModifiedTime() );
		Collection<UpdateRunIntent.Entry> entries = collectOutOfDateEntries( projectType, timeOfLastRun.get() );
		if( !entries.isEmpty() )
			return new UpdateRunIntent( timeOfLastRun.get(), entries );
		return UpToDateIntent.INSTANCE;
	}

	private static Collection<UpdateRunIntent.Entry> collectOutOfDateEntries( ProjectType projectType, Instant timeOfLastRun )
	{
		Collection<ProjectType> dependencies = getExpandedDependencies( projectType );
		List<UpdateRunIntent.Entry> entries = new ArrayList<>();
		for( ProjectType dependency : dependencies )
			if( dependency.getLastModifiedTime().isAfter( timeOfLastRun ) )
				entries.add( new UpdateRunIntent.Entry( dependency.className(), dependency.getLastModifiedTime() ) );
		return entries;
	}

	private static Collection<ProjectType> getExpandedDependencies( ProjectType projectType )
	{
		Collection<ProjectType> expandedDependencies = new HashSet<>();
		Deque<ProjectType> todo = new ArrayDeque<>( projectType.dependencies() );
		for( ProjectType dependency = todo.poll(); dependency != null; dependency = todo.poll() )
		{
			if( dependency == projectType )
				continue;
			if( !Kit.collection.tryAdd( expandedDependencies, dependency ) )
				continue;
			todo.addAll( dependency.dependencies() );
		}
		return expandedDependencies;
	}

	private static int compareTestClasses( TestClass a, TestClass b, Map<TestClass,Map<ProjectType,Integer>> dependencyDistanceGraph )
	{
		Optional<Map<ProjectType,Integer>> aDependencyDistances = Optional.ofNullable( Kit.map.tryGet( dependencyDistanceGraph, a ) );
		Optional<Map<ProjectType,Integer>> bDependencyDistances = Optional.ofNullable( Kit.map.tryGet( dependencyDistanceGraph, b ) );
		if( aDependencyDistances.isEmpty() && bDependencyDistances.isEmpty() )
			return 0;
		if( aDependencyDistances.isEmpty() )
			return -1;
		if( bDependencyDistances.isEmpty() )
			return 1;
		return compareDistances( aDependencyDistances.get(), bDependencyDistances.get() );
	}

	private static int compareDistances( Map<ProjectType,Integer> aDependencyDistances, Map<ProjectType,Integer> bDependencyDistances )
	{
		/* find all dependencies in common, skipping equidistant dependencies. */
		List<ProjectType> commonDependencies = new ArrayList<>();
		for( Map.Entry<ProjectType,Integer> entry : aDependencyDistances.entrySet() )
		{
			ProjectType dependency = entry.getKey();
			if( !bDependencyDistances.containsKey( dependency ) )
				continue;
			Integer aDistance = entry.getValue();
			Integer bDistance = Kit.map.tryGet( bDependencyDistances, dependency );
			if( aDistance.equals( bDistance ) )
				continue;
			commonDependencies.add( dependency );
		}
		if( commonDependencies.isEmpty() )
			return 0;

		/* find the 'closest' common dependency. That's the (common, non-equidistant) dependency with the shortest distance to either a or b. */
		ProjectType aClosestDependency = findClosestDependency( commonDependencies, aDependencyDistances );
		int aClosestDistance = Kit.map.get( aDependencyDistances, aClosestDependency );
		ProjectType bClosestDependency = findClosestDependency( commonDependencies, bDependencyDistances );
		int bClosestDistance = Kit.map.get( bDependencyDistances, bClosestDependency );
		ProjectType closestCommonDependency = aClosestDistance <= bClosestDistance ? aClosestDependency : bClosestDependency;

		/* if the 'closest' dependency is closer to a, then a > b.  If the 'closest' dependency is closer to b, then a < b. */
		int aDistance = Kit.map.get( aDependencyDistances, closestCommonDependency );
		int bDistance = Kit.map.get( bDependencyDistances, closestCommonDependency );
		assert aDistance != bDistance;
		return Integer.compare( aDistance, bDistance );
	}

	/* find the shortest dependency distance. That's the distance to the 'closest' dependency. */
	private static ProjectType findClosestDependency( Iterable<ProjectType> dependencies, Map<ProjectType,Integer> dependencyDistances )
	{
		int shortestDistance = Integer.MAX_VALUE;
		ProjectType closestDependency = null;
		for( ProjectType dependency : dependencies )
		{
			int distance = Kit.map.get( dependencyDistances, dependency );
			if( distance < shortestDistance )
			{
				closestDependency = dependency;
				shortestDistance = distance;
			}
		}
		return closestDependency;
	}

	/**
	 * Calculate dependency distance graph.
	 * This tells us, for each class, what classes it depends on, and for each one of them,
	 * what is the smallest number of hops we have to make to arrive at it.
	 */
	private static Map<TestClass,Map<ProjectType,Integer>> createDependencyDistanceGraph( Iterable<TestClass> testClasses )
	{
		Map<TestClass,Map<ProjectType,Integer>> dependencyDistanceGraph = new HashMap<>();
		for( TestClass testClass : testClasses )
		{
			Map<ProjectType,Integer> dependencyDistanceTable = createDependencyDistanceTable( testClass.projectType );
			Kit.map.add( dependencyDistanceGraph, testClass, dependencyDistanceTable );
		}
		return dependencyDistanceGraph;
	}

	private static Map<ProjectType,Integer> createDependencyDistanceTable( ProjectType projectType )
	{
		Map<ProjectType,Integer> dependencyDistanceTable = new HashMap<>();
		add( projectType.dependencies(), 1, dependencyDistanceTable );
		return dependencyDistanceTable;
	}

	private static void add( Iterable<ProjectType> projectTypes, int depth, Map<ProjectType,Integer> dependencyDistanceTable )
	{
		int sizeBefore = dependencyDistanceTable.size();
		for( ProjectType dependency : projectTypes )
		{
			if( dependencyDistanceTable.containsKey( dependency ) )
				continue;
			Kit.map.add( dependencyDistanceTable, dependency, depth );
		}
		int sizeAfter = dependencyDistanceTable.size();
		if( sizeBefore == sizeAfter )
			return;
		depth++;
		for( ProjectType dependency : projectTypes )
			add( dependency.dependencies(), depth, dependencyDistanceTable );
	}
}
