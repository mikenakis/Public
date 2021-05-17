package mikenakis.testana.test_engines.junit;

import mikenakis.kit.Kit;
import mikenakis.kit.logging.Log;
import mikenakis.testana.AncestryOrdering;
import mikenakis.testana.MethodOrdering;
import mikenakis.testana.TestEngine;
import mikenakis.testana.kit.ChainingComparator;
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
		return collectTestMethodsRecursive( javaClass, null, null, null, true );
	}

	@Override public TestClass createTestClass( ProjectType projectType )
	{
		Class<?> javaClass = projectType.javaClass();
		assert !Modifier.isAbstract( javaClass.getModifiers() );
		Optional<Constructor<?>> constructor = tryGetPublicParameterlessConstructor( javaClass );
		assert constructor.isPresent();
		Collection<Method> mutableTestMethods = new HashSet<>();
		Collection<Method> mutableBeforeMethods = new ArrayList<>();
		Collection<Method> mutableAfterMethods = new ArrayList<>();
		boolean ok = collectTestMethodsRecursive( javaClass, mutableTestMethods, mutableBeforeMethods, mutableAfterMethods, false );
		assert ok;
		//assert !mutableTestMethods.isEmpty();
		Comparator<Method> comparator = ( a, b ) -> 0;
		switch( methodOrdering )
		{
			case None:
				break;
			case ByNaturalOrder:
				comparator = new ChainingComparator<>( comparator, new NaturalOrderMethodComparator( method -> projectType.getDeclaredMethodIndex( method.getName() ) ) );
				break;
			default:
				assert false;
		}
		switch( ancestryOrdering )
		{
			case None:
				break;
			case AncestorFirst:
				comparator = new ChainingComparator<>( comparator, new JuniorityMethodComparator() );
				break;
			default:
				assert false;
		}
		List<Method> mutableTestMethodList = new ArrayList<>( mutableTestMethods );
		mutableTestMethodList.sort( comparator );
		return new JunitTestClass( this, projectType, constructor.get(), mutableTestMethodList, mutableBeforeMethods, mutableAfterMethods );
	}

	private static Optional<Constructor<?>> tryGetPublicParameterlessConstructor( Class<?> javaClass )
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

	private static boolean collectTestMethodsRecursive( Class<?> javaClass, Collection<Method> mutableTestMethods, Collection<Method> mutableBeforeMethods, //
			Collection<Method> mutableAfterMethods, boolean probeOnly )
	{
		boolean isTestClass = false;
		Class<?> superClass = javaClass.getSuperclass();
		if( superClass != null )
		{
			isTestClass = collectTestMethodsRecursive( superClass, mutableTestMethods, mutableBeforeMethods, mutableAfterMethods, probeOnly );
			if( isTestClass && probeOnly )
				return true;
		}
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
			if( (isBefore ? 1 : 0) + (isAfter ? 1 : 0) + (isTest ? 1 : 0) > 1 )
				Log.warning( "Test method " + javaClass.getName() + "." + javaMethod.getName() + " has conflicting annotations." );
			isTestClass |= isTest || isBefore || isAfter;
			if( isTestClass && probeOnly )
				return true;
			if( isTest )
			{
				if( !isOfSuitableSignature( javaMethod ) )
				{
					Log.warning( "Test method " + javaClass.getName() + "." + javaMethod.getName() + " should accept no arguments and return void." );
					continue;
				}
				Kit.collection.add( mutableTestMethods, javaMethod );
			}
			else if( isBefore )
			{
				if( !isOfSuitableSignature( javaMethod ) )
				{
					Log.warning( "Test method " + javaClass.getName() + "." + javaMethod.getName() + " should accept no arguments and return void." );
					continue;
				}
				Kit.collection.add( mutableBeforeMethods, javaMethod );
			}
			else if( isAfter )
			{
				if( !isOfSuitableSignature( javaMethod ) )
				{
					Log.warning( "Test method " + javaClass.getName() + "." + javaMethod.getName() + " should accept no arguments and return void." );
					continue;
				}
				Kit.collection.add( mutableAfterMethods, javaMethod );
			}
		}
		return isTestClass;
	}

	private static boolean isOfSuitableSignature( Method javaMethod )
	{
		if( javaMethod.getParameterCount() != 0 )
			return false;
		if( javaMethod.getReturnType() != void.class )
			return false;
		return true;
	}
}
