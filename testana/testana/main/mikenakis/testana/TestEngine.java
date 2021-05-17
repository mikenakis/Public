package mikenakis.testana;

import mikenakis.testana.structure.ProjectType;
import mikenakis.testana.testplan.TestClass;

/**
 * Test Engine.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class TestEngine
{
	private final String name;

	protected TestEngine( String name )
	{
		this.name = name;
	}

	public String name()
	{
		return name;
	}

	public abstract boolean isTestClass( Class<?> javaClass );

	public abstract TestClass createTestClass( ProjectType projectType );
}
