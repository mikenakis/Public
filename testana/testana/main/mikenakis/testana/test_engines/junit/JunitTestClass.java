package mikenakis.testana.test_engines.junit;

import mikenakis.kit.Kit;
import mikenakis.kit.logging.Log;
import mikenakis.testana.AncestryOrdering;
import mikenakis.testana.MethodOrdering;
import mikenakis.testana.structure.ProjectType;
import mikenakis.testana.testplan.TestClass;
import mikenakis.testana.testplan.TestMethod;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
		Optional<Constructor<?>> constructor = JunitTestEngine.tryGetPublicParameterlessConstructor( javaClass );
		defaultConstructor = constructor.orElseThrow();
		Collection<JunitTestMethod> mutableTestMethods = new HashSet<>();
		Collection<Method> mutableBeforeMethods = new ArrayList<>();
		Collection<Method> mutableAfterMethods = new ArrayList<>();
		collectTestMethodsRecursive( javaClass, 0, mutableTestMethods, mutableBeforeMethods, mutableAfterMethods );
		//assert !mutableTestMethods.isEmpty();
		List<JunitTestMethod> sortableTestMethods = new ArrayList<>( mutableTestMethods );
		beforeJavaMethods = mutableBeforeMethods;
		afterJavaMethods = mutableAfterMethods;

		Comparator<JunitTestMethod> comparator = Comparator.comparing( method -> method.javaMethod.getName() );
		switch( methodOrdering )
		{
			case None:
				break;
			case ByNaturalOrder:
				comparator = getNaturalMethodOrderComparator( projectType, mutableTestMethods.size(), sortableTestMethods ).thenComparing( comparator );
				break;
			default:
				assert false;
		}
		switch( ancestryOrdering )
		{
			case None:
				break;
			case AncestorFirst:
				comparator = Comparator.<JunitTestMethod>comparingInt( method -> -method.derivationDepth ).thenComparing( comparator );
				break;
			default:
				assert false;
		}
		if( sortableTestMethods.size() > 1 )
			Kit.get( true );
		sortableTestMethods.sort( comparator );
		testMethods = sortableTestMethods;
	}

	private Comparator<JunitTestMethod> getNaturalMethodOrderComparator( ProjectType projectType, int testMethodCount, Collection<JunitTestMethod> junitTestMethods )
	{
		return new NaturalOrderMethodComparator( /*method -> //
		{
			Class<?> declaringClass = method.javaMethod.getDeclaringClass();
			Optional<ProjectType> declaringProjectType = projectType.projectModule.tryGetProjectTypeByName( declaringClass.getTypeName() );
			int index = declaringProjectType.orElseThrow().getMethodIndex( method.javaMethod.getName() );
			assert index >= 0 && index < testMethodCount;
			return index;
		}*/ );
	}

	private static <T> Optional<Comparator<T>> addComparator( Optional<Comparator<T>> comparator, Comparator<T> additionalComparator )
	{
		return Optional.of( comparator.map( additionalComparator::thenComparing ).orElse( additionalComparator ) );
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
				Optional<ProjectType> declaringProjectType = projectType.projectModule.tryGetProjectTypeByName( javaClass.getTypeName() );
				int methodIndex = declaringProjectType.map( t -> t.getMethodIndex( javaMethod.getName() ) ).orElse( 0 );
				JunitTestMethod junitTestMethod = new JunitTestMethod( this, javaMethod, ignored, derivationDepth, methodIndex );
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
		return testMethods.stream().map( c -> (TestMethod)c ).collect( Collectors.toList() );
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
