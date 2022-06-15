package io.github.mikenakis.testana.test_engines.junit;

import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.kit.logging.Log;
import io.github.mikenakis.testana.AncestryOrdering;
import io.github.mikenakis.testana.MethodOrdering;
import io.github.mikenakis.testana.structure.ProjectType;
import io.github.mikenakis.testana.testplan.TestClass;
import io.github.mikenakis.testana.testplan.TestMethod;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

/**
 * JUnit {@link TestClass}.
 *
 * @author michael.gr
 */
class JunitTestClass extends TestClass
{
	final JunitTestEngine junitTestEngine;
	final Constructor<?> defaultConstructor;
	final Collection<Method> beforeJavaMethods;
	final Collection<Method> afterJavaMethods;
	private final List<JunitTestMethod> testMethods;
	private final boolean ignored;

	JunitTestClass( JunitTestEngine junitTestEngine, ProjectType projectType, MethodOrdering methodOrdering, AncestryOrdering ancestryOrdering )
	{
		super( projectType );
		this.junitTestEngine = junitTestEngine;
		ignored = Kit.reflect.hasAnnotation( projectType.javaClass(), "org.junit.Ignore" );
		Class<?> javaClass = projectType.javaClass();
		assert !Modifier.isAbstract( javaClass.getModifiers() );
		defaultConstructor = JunitTestEngine.tryGetPublicParameterlessConstructor( javaClass ).orElseThrow();
		Collection<JunitTestMethod> mutableTestMethods = new HashSet<>();
		Collection<Method> mutableBeforeMethods = new ArrayList<>();
		Collection<Method> mutableAfterMethods = new ArrayList<>();
		collectTestMethodsRecursive( javaClass, 0, mutableTestMethods, mutableBeforeMethods, mutableAfterMethods );
		assert !mutableTestMethods.isEmpty();
		beforeJavaMethods = mutableBeforeMethods;
		afterJavaMethods = mutableAfterMethods;
		testMethods = sort( mutableTestMethods, methodOrdering, ancestryOrdering );
	}

	private List<JunitTestMethod> sort( Collection<JunitTestMethod> mutableTestMethods, MethodOrdering methodOrdering, AncestryOrdering ancestryOrdering )
	{
		List<JunitTestMethod> sortableTestMethods = new ArrayList<>( mutableTestMethods );
		Comparator<JunitTestMethod> comparator = Comparator.comparing( method -> method.javaMethod.getName() );
		comparator = applyAncestryOrdering( comparator, ancestryOrdering );
		comparator = applyMethodOrdering( comparator, methodOrdering, projectType, sortableTestMethods );
		sortableTestMethods.sort( comparator );
		sortableTestMethods.sort( (a,b) -> //
		{
			int d = Integer.compare( a.derivationDepth, b.derivationDepth );
			if( ancestryOrdering == AncestryOrdering.Normal )
			    d = -d;
			if( d != 0 )
				return d;
			if( methodOrdering == MethodOrdering.Natural )
				d = Integer.compare( a.methodIndex, b.methodIndex );
			else
				d = a.javaMethod.getName().compareTo( b.javaMethod.getName() );
			return d;
		} );
		return sortableTestMethods;
	}

	private static Comparator<JunitTestMethod> applyMethodOrdering( Comparator<JunitTestMethod> comparator, MethodOrdering methodOrdering, ProjectType projectType, List<JunitTestMethod> sortableTestMethods )
	{
		switch( methodOrdering )
		{
			case Alphabetic:
				return comparator;
			case Natural:
				return new NaturalOrderMethodComparator( /*method -> //
				{
					int testMethodCount = junitTestMethods.size();
					Class<?> declaringClass = method.javaMethod.getDeclaringClass();
					Optional<ProjectType> declaringProjectType = projectType.projectModule.tryGetProjectTypeByName( declaringClass.getTypeName() );
					int index = declaringProjectType.orElseThrow().getMethodIndex( method.javaMethod.getName() );
					assert index >= 0 && index < testMethodCount;
					return index;
				}*/ ).thenComparing( comparator );
			default:
				assert false;
				return null;
		}
	}

	private static Comparator<JunitTestMethod> applyAncestryOrdering( Comparator<JunitTestMethod> comparator, AncestryOrdering ancestryOrdering )
	{
		switch( ancestryOrdering )
		{
			case Backwards:
				return comparator;
			case Normal:
				return Comparator.<JunitTestMethod>comparingInt( method -> -method.derivationDepth ).thenComparing( comparator );
			default:
				assert false;
				return null;
		}
	}

	private void collectTestMethodsRecursive( Class<?> javaClass, int derivationDepth, Collection<JunitTestMethod> mutableJunitTestMethods, //
		Collection<Method> mutablePreTestJavaMethods, Collection<Method> mutablePostTestJavaMethods )
	{
		Class<?> superClass = javaClass.getSuperclass();
		if( superClass != null )
			collectTestMethodsRecursive( superClass, derivationDepth + 1, mutableJunitTestMethods, mutablePreTestJavaMethods, mutablePostTestJavaMethods );
		Method[] declaredMethods = javaClass.getDeclaredMethods();
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
			if( (isBefore ? 1 : 0) + (isAfter ? 1 : 0) + (isTest ? 1 : 0) > 1 )
				Log.warning( "Test method " + javaClass.getName() + "." + javaMethod.getName() + " has conflicting annotations." );
			if( isTest )
			{
				if( !isOfSuitableSignature( javaMethod ) )
				{
					Log.warning( "Test method " + javaClass.getName() + "." + javaMethod.getName() + " should accept no arguments and return void." );
					continue;
				}
				JunitTestMethod junitTestMethod = getJunitTestMethod( javaClass, derivationDepth, javaMethod );
				Kit.collection.add( mutableJunitTestMethods, junitTestMethod );
			}
			else if( isBefore )
			{
				if( !isOfSuitableSignature( javaMethod ) )
				{
					Log.warning( "Test method " + javaClass.getName() + "." + javaMethod.getName() + " should accept no arguments and return void." );
					continue;
				}
				Kit.collection.add( mutablePreTestJavaMethods, javaMethod );
			}
			else if( isAfter )
			{
				if( !isOfSuitableSignature( javaMethod ) )
				{
					Log.warning( "Test method " + javaClass.getName() + "." + javaMethod.getName() + " should accept no arguments and return void." );
					continue;
				}
				Kit.collection.add( mutablePostTestJavaMethods, javaMethod );
			}
		}
	}

	private JunitTestMethod getJunitTestMethod( Class<?> javaClass, int derivationDepth, Method javaMethod )
	{
		ProjectType declaringProjectType = projectType.projectModule.projectStructure.getProjectTypeByName( javaClass.getTypeName() );
		int methodIndex = declaringProjectType.getMethodIndex( javaMethod.getName() );
		return new JunitTestMethod( this, javaMethod, ignored, derivationDepth, methodIndex );
	}

	private static boolean isOfSuitableSignature( Method javaMethod )
	{
		if( javaMethod.getParameterCount() != 0 )
			return false;
		if( javaMethod.getReturnType() != void.class )
			return false;
		return true;
	}

	String getMethodSourceLocation( Method javaMethod )
	{
		return projectType.getMethodSourceLocation( javaMethod.getDeclaringClass().getName(), javaMethod.getName() );
	}

	@Override public Collection<TestMethod> testMethods()
	{
		return List.copyOf( testMethods );
	}

	@Override public String toString()
	{
		var builder = new StringBuilder();
		builder.append( "JUnit test class " );
		builder.append( fullName() );
		if( ignored )
			builder.append( " ignored" );
		return builder.toString();
	}
}
