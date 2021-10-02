package mikenakis.testana.test_engines.junit;

import mikenakis.kit.Kit;
import mikenakis.kit.functional.Function0;
import mikenakis.testana.runtime.result.FailedMethodTestResult;
import mikenakis.testana.runtime.result.IgnoredMethodTestResult;
import mikenakis.testana.runtime.result.SucceededMethodTestResult;
import mikenakis.testana.runtime.result.TestMethodResult;
import mikenakis.testana.testplan.TestMethod;

import java.io.BufferedOutputStream;
import java.io.PrintStream;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;

/**
 * JUnit {@link TestMethod}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
class JunitTestMethod extends TestMethod
{
	private final JunitTestClass junitTestClass;
	private final Method javaMethod;
	private final boolean ignored;

	JunitTestMethod( JunitTestClass junitTestClass, Method javaMethod, boolean ignored )
	{
		this.junitTestClass = junitTestClass;
		this.javaMethod = javaMethod;
		this.ignored = ignored || Kit.reflect.hasAnnotation( javaMethod, "org.junit.Ignore" );
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
		Object returnValue = Kit.invokeThrowableThrowingFunction( () -> handle.invoke( instance ) );
		assert returnValue == null; //a method handle of a method with return type void is always expected to return null.
	}

	private static <T> T withRedirectedOutput( Function0<T> procedure )
	{
		PrintStream oldSystemOut = System.out;
		PrintStream newSystemOut = new PrintStream( LinePrefixingOutputStream.of( oldSystemOut, "\\~" ), false );
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
