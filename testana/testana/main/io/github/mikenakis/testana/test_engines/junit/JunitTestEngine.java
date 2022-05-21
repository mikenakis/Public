package io.github.mikenakis.testana.test_engines.junit;

import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.kit.logging.Log;
import io.github.mikenakis.testana.AncestryOrdering;
import io.github.mikenakis.testana.MethodOrdering;
import io.github.mikenakis.testana.TestEngine;
import io.github.mikenakis.testana.structure.ProjectType;
import io.github.mikenakis.testana.testplan.TestClass;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;

/**
 * JUnit {@link TestEngine}.
 *
 * @author michael.gr
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
