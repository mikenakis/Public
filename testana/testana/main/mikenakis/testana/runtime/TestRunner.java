package mikenakis.testana.runtime;

import mikenakis.kit.Kit;
import mikenakis.testana.Persistence;
import mikenakis.testana.kit.TestanaLog;
import mikenakis.testana.kit.TimeMeasurement;
import mikenakis.testana.runtime.result.NonLeafTestResult;
import mikenakis.testana.runtime.result.TestMethodResult;
import mikenakis.testana.runtime.result.TestResult;
import mikenakis.testana.testplan.TestClass;
import mikenakis.testana.testplan.TestMethod;
import mikenakis.testana.testplan.TestModule;
import mikenakis.testana.testplan.TestPlan;
import mikenakis.testana.testplan.intent.Intent;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Runner.
 * <p>
 * TODO: add tests for this class!
 *
 * @author michael.gr
 */
public final class TestRunner
{
	public static TestResult run( TestPlan testPlan, Persistence persistence )
	{
		TestResult testResult = TimeMeasurement.run( "Running tests", "Ran %d tests in %d classes", timeMeasurement -> //
		{
			List<TestResult> testResults = new ArrayList<>();
			for( TestModule testModule : testPlan.testModules() )
			{
				if( !testPlan.isToRun( testModule ) )
					continue;

				TestanaLog.report( indentation( 1 ) + "Test module " + testModule.toString() );
				for( TestClass testClass : testModule.testClasses() )
				{
					Intent intent = testPlan.getIntent( testClass );
					if( intent.isToRun() )
					{
						TestResult nonLeafTestResult = runTestClass( testClass, persistence, testModule.sourcePath() );
						testResults.add( nonLeafTestResult );
					}
				}
			}
			NonLeafTestResult rootTestResult = new NonLeafTestResult( "root", testResults );
			timeMeasurement.setArguments( rootTestResult.successCount() + rootTestResult.failureCount(), testPlan.testClassCount() );
			return rootTestResult;
		} );

		/* at the end of all tests, run garbage collection to detect any leaks */
		Kit.runGarbageCollection();

		return testResult;
	}

	private static NonLeafTestResult runTestClass( TestClass testClass, Persistence persistence, Path userdir )
	{
		TestanaLog.report( indentation( 2 ) + "Test class " + testClass.projectType.getClassSourceLocation() );
		List<TestMethodResult> childTestResults = new ArrayList<>();
		for( TestMethod testMethod : testClass.testMethods() )
		{
			TestMethodResult testMethodResult = runTestMethod( testMethod, userdir );
			childTestResults.add( testMethodResult );
		}
		NonLeafTestResult result = new NonLeafTestResult( testClass.fullName(), childTestResults );
		if( result.failureCount() > 0 )
			TestanaLog.report( indentation( 2 ) + testClass.fullName() + " " + result.getOutcomeMessage() );
		else
			persistence.setTimeOfLastRun( testClass.fullName(), Instant.now() );
		return result;
	}

	private static TestMethodResult runTestMethod( TestMethod testMethod, Path userDir )
	{
		TestanaLog.report( indentation( 3 ) + "Test method " + testMethod.getSourceLocation() );
		String oldUserDir = System.getProperty( "user.dir" );
		System.setProperty( "user.dir", userDir.toString() );
		TestMethodResult methodTestResult = testMethod.run();
		System.setProperty( "user.dir", oldUserDir );
		if( methodTestResult.ignoredCount() > 0 || methodTestResult.failureCount() > 0 )
			TestanaLog.report( indentation( 4 ) + methodTestResult.getOutcomeMessage() );
		return methodTestResult;
	}

	private static String indentation( int count )
	{
		return "    ".repeat( count );
	}
}
