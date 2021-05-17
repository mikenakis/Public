package mikenakis.testana.testplan;

import mikenakis.kit.Kit;
import mikenakis.testana.structure.ProjectModule;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Represents a test module.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class TestModule
{
	private final ProjectModule projectModule;
	private final List<TestClass> testClasses; //this is a list because we may sort it.

	TestModule( ProjectModule projectModule, Collection<TestClass> testClasses )
	{
		this.projectModule = projectModule;
		this.testClasses = new ArrayList<>( testClasses );
	}

	public final String name()
	{
		return projectModule.name();
	}

	public Collection<TestClass> testClasses()
	{
		return testClasses;
	}

	public Path sourcePath()
	{
		return projectModule.sourcePath();
	}

	@Override public String toString()
	{
		return projectModule.toString();
	}

	ProjectModule projectModule()
	{
		return projectModule;
	}

	void sortTestClasses( Comparator<TestClass> comparator )
	{
		Kit.sort.quickSort( testClasses, comparator );
	}
}
