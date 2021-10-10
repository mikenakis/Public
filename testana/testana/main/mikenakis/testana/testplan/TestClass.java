package mikenakis.testana.testplan;

import mikenakis.testana.structure.ProjectType;

import java.util.Collection;

/**
 * Represents a test class.
 *
 * @author michael.gr
 */
public abstract class TestClass
{
	public final ProjectType projectType;

	protected TestClass( ProjectType projectType )
	{
		this.projectType = projectType;
	}

	public final String fullName()
	{
		return projectType.className();
	}

	public final String simpleName()
	{
		return projectType.simpleName();
	}

	public abstract Collection<TestMethod> testMethods();
}
