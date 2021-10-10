package mikenakis.testana.testplan;

import mikenakis.testana.runtime.result.TestMethodResult;

/**
 * Represents a test method.
 *
 * @author michael.gr
 */
public abstract class TestMethod
{
	public abstract TestMethodResult run();
	public abstract String name();
	public abstract String getSourceLocation();
}
