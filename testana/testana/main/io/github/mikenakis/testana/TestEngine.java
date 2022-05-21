package io.github.mikenakis.testana;

import io.github.mikenakis.testana.structure.ProjectType;
import io.github.mikenakis.testana.testplan.TestClass;

/**
 * Test Engine.
 *
 * @author michael.gr
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
