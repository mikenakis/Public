package mikenakis.testana.test_engines.junit;

import mikenakis.kit.Kit;
import mikenakis.kit.logging.Log;
import mikenakis.testana.AncestryOrdering;
import mikenakis.testana.MethodOrdering;
import mikenakis.testana.TestEngine;
import mikenakis.testana.structure.ProjectType;
import mikenakis.testana.testplan.TestClass;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

/**
 * JUnit {@link TestEngine}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class JunitTestEngine extends TestEngine
{
	final MethodHandles.Lookup lookup = MethodHandles.lookup();
	private final MethodOrdering methodOrdering;
	private final AncestryOrdering ancestryOrdering;

	public JunitTestEngine( MethodOrdering methodOrdering, AncestryOrdering ancestryOrdering )
	{
		super( "JUnit" );
		this.methodOrdering = methodOrdering;
		this.ancestryOrdering = ancestryOrdering;
	}

	@Override public boolean isTestClass( Class<?> javaClass )
	{
		if( Modifier.isAbstract( javaClass.getModifiers() ) )
			return false;
		Optional<Constructor<?>> constructor = tryGetPublicParameterlessConstructor( javaClass );
		if( constructor.isEmpty() )
			return false;
		return isTestClassRecursive( javaClass );
	}

	private static boolean isTestClassRecursive( Class<?> javaClass )
	{
		Class<?> superClass = javaClass.getSuperclass();
		if( superClass != null )
			if( isTestClassRecursive( superClass ) )
				return true;
		Method[] declaredMethods;
		try
		{
			declaredMethods = javaClass.getDeclaredMethods();
		}
		catch( Throwable e )
		{
			Log.warning( "could not get declared methods of '" + javaClass.getName() + "': " + e.getClass().getName() + ": " + e.getMessage() );
			return false;
		}
		for( Method javaMethod : declaredMethods )
		{
			if( !Modifier.isPublic( javaMethod.getModifiers() ) )
				continue;
			if( Modifier.isStatic( javaMethod.getModifiers() ) )
				continue;
			if( Modifier.isAbstract( javaMethod.getModifiers() ) )
				continue;
			boolean isTest = Kit.reflect.hasAnnotation( javaMethod, "org.junit.Test" );
			boolean isBefore = Kit.reflect.hasAnnotation( javaMethod, "org.junit.Before" );
			boolean isAfter = Kit.reflect.hasAnnotation( javaMethod, "org.junit.After" );
			if( isTest || isBefore || isAfter )
				return true;
		}
		return false;
	}

	@Override public TestClass createTestClass( ProjectType projectType )
	{
		return new JunitTestClass( this, projectType, methodOrdering, ancestryOrdering );
	}

	static Optional<Constructor<?>> tryGetPublicParameterlessConstructor( Class<?> javaClass )
	{
		/* search for the constructor instead of invoking getConstructor() so as to avoid throwing a 'NoSuchMethodException'. */
		Constructor<?>[] constructors;
		try
		{
			constructors = javaClass.getConstructors();
		}
		catch( Throwable e )
		{
			Log.warning( "Could not get constructors of '" + javaClass.getName() + "': " + e.getClass().getName() + ": " + e.getMessage() );
			return Optional.empty();
		}
		for( Constructor<?> constructor : constructors )
		{
			if( constructor.getParameterCount() == 0 )
				return Optional.of( constructor );
		}
		return Optional.empty();
	}
}
