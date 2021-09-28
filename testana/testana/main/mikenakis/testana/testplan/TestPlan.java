package mikenakis.testana.testplan;

import mikenakis.kit.Kit;
import mikenakis.kit.functional.Function1;
import mikenakis.testana.kit.TestanaLog;
import mikenakis.testana.testplan.intent.Intent;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Contains {@link TestModule}s, {@link TestClass}s, {@link TestMethod}s and {@link Intent}s.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class TestPlan
{
	public enum ShowOption
	{
		None, Normal, Verbose
	}

	private final Collection<TestModule> testModules;
	private final Map<TestClass,Intent> testClassIntents;

	TestPlan( Collection<TestModule> testModules, Map<TestClass,Intent> testClassIntents )
	{
		assert testClassesHaveIntentsAssertion( testModules, testClassIntents );
		this.testModules = testModules;
		this.testClassIntents = testClassIntents;
	}

	private static boolean testClassesHaveIntentsAssertion( Iterable<TestModule> testModules, Map<TestClass,Intent> testClassIntents )
	{
		for( TestModule testModule : testModules )
			for( TestClass testClass : testModule.testClasses() )
				assert testClassIntents.containsKey( testClass );
		return true;
	}

	public Collection<TestModule> testModules()
	{
		return testModules;
	}

	public int testClassCount()
	{
		return testClassIntents.size();
	}

	public Intent getIntent( TestClass testClass )
	{
		return Kit.map.get( testClassIntents, testClass );
	}

	public boolean isToRun( TestModule testModule )
	{
		for( TestClass testClass : testModule.testClasses() )
		{
			Intent intent = getIntent( testClass );
			if( intent.isToRun() )
				return true;
		}
		return false;
	}

	public void show( ShowOption showOption )
	{
		if( showOption != ShowOption.None )
		{
			Function1<Iterable<Object>,Object> breeder = object -> //
			{
				if( object instanceof TestPlan testPlan )
					return Kit.iterable.downCast( testPlan.testModules );
				if( object instanceof TestModule testModule )
					return Kit.iterable.downCast( testModule.testClasses() );
				if( object instanceof TestClass testClass )
					return switch( showOption )
					{
						//noinspection ConstantConditions
						case None -> { assert false; yield null; } //should not have been here.
						case Normal -> List.of();
						case Verbose -> Kit.iterable.downCast( testClass.testMethods() );
					};
				if( object instanceof TestMethod )
					return List.of();
				assert false;
				return null;
			};
			Function1<String,Object> stringizer = object -> //
			{
				if( object instanceof TestPlan )
					return "TestPlan";
				if( object instanceof TestModule testModule )
					return "Module: " + testModule.name();
				if( object instanceof TestClass testClass )
					return testClass.toString() + " " + getIntent( testClass );
				if( object instanceof TestMethod testMethod )
					return testMethod.toString();
				assert false;
				return null;
			};
			Kit.tree.print( this, breeder, stringizer, TestanaLog::report );
		}
	}

	@Override public String toString()
	{
		return testClassIntents.size() + " entries";
	}
}
