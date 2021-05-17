package mikenakis.testana.testplan;

import mikenakis.testana.structure.ProjectType;

import java.util.Collection;

/**
 * Represents a test class.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class TestClass
{
	public final ProjectType projectType;

	protected TestClass( ProjectType projectType )
	{
		this.projectType = projectType;
	}

	public final String name()
	{
		return projectType.className();
	}

	public abstract Collection<TestMethod> testMethods();
}
