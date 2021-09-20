package mikenakis.testana.testplan;

import mikenakis.kit.Kit;
import mikenakis.testana.kit.TestanaLog;
import mikenakis.testana.kit.textTree.TextTree;
import mikenakis.testana.testplan.intent.Intent;

import java.util.Collection;
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
			TextTree textTree = new TextTree( "TestPlan", TestanaLog::report );
			textTree.print( testModules, TestPlan::showModule, ( parent, testModule ) -> showChildrenOfModule( parent, testModule, showOption ) );
		}
	}

	private static String showModule( TestModule testModule )
	{
		return "Module: " + testModule.name();
	}

	private void showChildrenOfModule( TextTree textTree, TestModule testModule, ShowOption showOption )
	{
		textTree.print( testModule.testClasses(), this::showTestClass, ( parent, classTestNode ) -> showChildrenOfTestClass( parent, classTestNode, showOption ) );
	}

	private String showTestClass( TestClass testClass )
	{
		return testClass.toString() + " " + getIntent( testClass );
	}

	private static void showChildrenOfTestClass( TextTree textTree, TestClass testClass, ShowOption showOption )
	{
		switch( showOption )
		{
			case None -> { assert false; } //should not have been here.
			case Normal -> { }
			case Verbose -> textTree.print( testClass.testMethods(), TestMethod::toString );
		}
	}

	@Override public String toString()
	{
		return testClassIntents.size() + " entries";
	}
}
