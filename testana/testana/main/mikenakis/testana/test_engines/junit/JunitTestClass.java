package mikenakis.testana.test_engines.junit;

import mikenakis.kit.Kit;
import mikenakis.testana.structure.ProjectType;
import mikenakis.testana.testplan.TestClass;
import mikenakis.testana.testplan.TestMethod;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JUnit {@link TestClass}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
class JunitTestClass extends TestClass
{
	final JunitTestEngine junitTestEngine;
	final Constructor<?> defaultConstructor;
	final Collection<Method> beforeJavaMethods;
	final Collection<Method> afterJavaMethods;
	private final List<JunitTestMethod> testMethods;
	private final boolean ignored;

	JunitTestClass( JunitTestEngine junitTestEngine, ProjectType projectType, Constructor<?> defaultConstructor, //
		Collection<Method> javaMethods, Collection<Method> beforeJavaMethods, Collection<Method> afterJavaMethods )
	{
		super( projectType );
		this.junitTestEngine = junitTestEngine;
		this.defaultConstructor = defaultConstructor;
		ignored = Kit.reflect.hasAnnotation( projectType.javaClass(), "org.junit.Ignore" );
		testMethods = new ArrayList<>( javaMethods.size() );
		for( Method javaMethod : javaMethods )
		{
			JunitTestMethod testMethod = new JunitTestMethod( this, javaMethod, ignored );
			testMethods.add( testMethod );
		}
		this.beforeJavaMethods = beforeJavaMethods;
		this.afterJavaMethods = afterJavaMethods;
	}

	String getMethodSourceLocation( Method javaMethod )
	{
		return projectType.getMethodSourceLocation( javaMethod.getName() );
	}

	@Override public Collection<TestMethod> testMethods()
	{
		return testMethods.stream().map( c -> (TestMethod)c ).collect( Collectors.toList() );
	}

	@Override public String toString()
	{
		var builder = new StringBuilder();
		builder.append( "JUnit test class " );
		builder.append( name() );
		if( ignored )
			builder.append( " ignored" );
		return builder.toString();
	}
}
