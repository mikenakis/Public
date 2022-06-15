package io.github.mikenakis.testana.test_engines.junit;

import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.kit.functional.Function0;
import io.github.mikenakis.testana.runtime.result.FailedMethodTestResult;
import io.github.mikenakis.testana.runtime.result.IgnoredMethodTestResult;
import io.github.mikenakis.testana.runtime.result.SucceededMethodTestResult;
import io.github.mikenakis.testana.runtime.result.TestMethodResult;
import io.github.mikenakis.testana.testplan.TestMethod;

import java.io.PrintStream;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;

/**
 * JUnit {@link TestMethod}.
 *
 * @author michael.gr
 */
class JunitTestMethod extends TestMethod
{
	private final JunitTestClass junitTestClass;
	final Method javaMethod;
	private final boolean ignored;
	final int derivationDepth;
	final int methodIndex;

	JunitTestMethod( JunitTestClass junitTestClass, Method javaMethod, boolean ignored, int derivationDepth, int methodIndex )
	{
		this.junitTestClass = junitTestClass;
		this.javaMethod = javaMethod;
		this.ignored = ignored || Kit.reflect.hasAnnotation( javaMethod, "org.junit.Ignore" );
		this.derivationDepth = derivationDepth;
		this.methodIndex = methodIndex;
	}

	@Override public String name()
	{
		return javaMethod.getName() + "()";
	}

	@Override public String getSourceLocation()
	{
		return junitTestClass.getMethodSourceLocation( javaMethod );
	}

	@Override public TestMethodResult run()
	{
		if( ignored )
			return new IgnoredMethodTestResult( name() );
		return withRedirectedOutput( () -> run0() );
	}

	private TestMethodResult run0()
	{
		Optional<Throwable> theThrowable = Kit.tryCatch( () -> //
		{
			Object instance = Kit.unchecked( () -> junitTestClass.defaultConstructor.newInstance() );
			for( Method method : junitTestClass.beforeJavaMethods )
				invokeMethod( instance, method );
			invokeMethod( instance, javaMethod );
			for( Method method : junitTestClass.afterJavaMethods )
				invokeMethod( instance, method );
		} );
		if( theThrowable.isPresent() )
			return new FailedMethodTestResult( name(), theThrowable.get() );
		return new SucceededMethodTestResult( name() );
	}

	private void invokeMethod( Object instance, Method method )
	{
		assert method.getReturnType() == Void.TYPE;
		assert method.getParameterCount() == 0;
		assert !Modifier.isStatic( method.getModifiers() );
		MethodHandle handle = Kit.unchecked( () -> junitTestClass.junitTestEngine.lookup.unreflect( method ) );
		Object returnValue = Kit.invokeThrowingFunction( () -> handle.invoke( instance ) );
		assert returnValue == null; //a method handle of a method with return type void is always expected to return null.
	}

	//TODO: this functionality is not junit-specific, it belongs to testana, so move it out of here and put it higher up the call tree, in the test runner.
	private static <T> T withRedirectedOutput( Function0<T> procedure )
	{
		PrintStream oldSystemOut = System.out;
		PrintStream newSystemOut = new PrintStream( LinePrefixingOutputStream.of( oldSystemOut, "\\~" ), true );
		System.setOut( newSystemOut );
		return Kit.tryFinally( procedure, () -> System.setOut( oldSystemOut ) );
	}

	@Override public String toString()
	{
		var builder = new StringBuilder();
		builder.append( "JUnit test method " );
		Class<?> javaClass = javaMethod.getDeclaringClass();
		if( javaClass != junitTestClass.projectType.javaClass() )
		{
			assert javaClass.isAssignableFrom( junitTestClass.projectType.javaClass() );
			builder.append( javaClass.getSimpleName() ).append( '.' );
		}
		builder.append( javaMethod.getName() ).append( "()" );
		if( ignored )
			builder.append( " ignored" );
		return builder.toString();
	}
}
