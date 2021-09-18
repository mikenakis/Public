package mikenakis.testana.runtime;

import mikenakis.testana.Persistence;
import mikenakis.testana.kit.TimeMeasurement;
import mikenakis.testana.kit.TestanaLog;
import mikenakis.testana.runtime.result.NonLeafTestResult;
import mikenakis.testana.runtime.result.TestMethodResult;
import mikenakis.testana.runtime.result.TestResult;
import mikenakis.testana.runtime.result.UpToDateMethodTestResult;
import mikenakis.testana.testplan.TestClass;
import mikenakis.testana.testplan.TestMethod;
import mikenakis.testana.testplan.TestModule;
import mikenakis.testana.testplan.TestPlan;
import mikenakis.testana.testplan.intent.Intent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Runner.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class TestRunner
{
	public static TestResult run( TestPlan testPlan, Persistence persistence )
	{
		return TimeMeasurement.run( "Running tests", "Ran %d tests in %d classes", timeMeasurement -> //
		{
			List<TestResult> testResults = new ArrayList<>();
			for( TestModule testModule : testPlan.testModules() )
			{
				if( !testPlan.isToRun( testModule ) )
					continue;

				TestanaLog.report( "    Test module " + testModule.toString() );
				System.setProperty( "user.dir", testModule.sourcePath().toString() );
				for( TestClass testClass : testModule.testClasses() )
				{
					Intent intent = testPlan.getIntent( testClass );
					TestResult nonLeafTestResult = runTestClass( testClass, intent, persistence );
					testResults.add( nonLeafTestResult );
				}
			}
			NonLeafTestResult rootTestResult = new NonLeafTestResult( "root", testResults );
			timeMeasurement.setArguments( rootTestResult.successCount() + rootTestResult.failureCount(), testPlan.testClassCount() );
			return rootTestResult;
		} );
	}

	private static NonLeafTestResult runTestClass( TestClass testClass, Intent intent, Persistence persistence )
	{
		if( intent.isToRun() )
			TestanaLog.report( "        Test class " + testClass.projectType.getClassSourceLocation() );
		List<TestMethodResult> childTestResults = new ArrayList<>();
		for( TestMethod testMethod : testClass.testMethods() )
		{
			TestMethodResult testMethodResult = runTestMethod( intent.isToRun(), testMethod );
			childTestResults.add( testMethodResult );
		}
		NonLeafTestResult result = new NonLeafTestResult( testClass.name(), childTestResults );
		if( intent.isToRun() )
		{
			if( result.failureCount() > 0 )
				TestanaLog.report( "        " + testClass.name() + " " + result.getOutcomeMessage() );
			else
				persistence.setTimeOfLastRun( testClass.name(), Instant.now() );
		}
		return result;
	}

	private static TestMethodResult runTestMethod( boolean isToRun, TestMethod testMethod )
	{
		if( isToRun )
			TestanaLog.report( "            Test method " + testMethod.getSourceLocation() );
		TestMethodResult methodTestResult;
		if( !isToRun )
			methodTestResult = new UpToDateMethodTestResult( testMethod.name() );
		else
			methodTestResult = testMethod.run();
		if( isToRun && methodTestResult.ignoredCount() > 0 || methodTestResult.failureCount() > 0 )
			TestanaLog.report( "                " + methodTestResult.getOutcomeMessage() );
		return methodTestResult;
	}
}
