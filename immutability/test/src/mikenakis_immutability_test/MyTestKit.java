package mikenakis_immutability_test;

import mikenakis.debug.Debug;

import java.util.Collection;
import java.util.List;

final class MyTestKit
{
	private MyTestKit()
	{
	}

	static <T extends Throwable> T expect( Class<T> expectedThrowableClass, Runnable procedure )
	{
		Throwable caughtThrowable = invokeAndCatch( procedure );
		assert caughtThrowable != null : expectedThrowableClass;
		caughtThrowable = unwrap( caughtThrowable );
		assert caughtThrowable.getClass() == expectedThrowableClass : caughtThrowable;
		return expectedThrowableClass.cast( caughtThrowable );
	}

	private static Throwable unwrap( Throwable throwable )
	{
		while( throwable instanceof AssertionError && throwable.getCause() != null )
			throwable = throwable.getCause();
		return throwable;
	}

	private static Throwable invokeAndCatch( Runnable procedure )
	{
		assert !Debug.expectingException;
		Debug.expectingException = true;
		try
		{
			procedure.run();
			return null;
		}
		catch( Throwable throwable )
		{
			return throwable;
		}
		finally
		{
			assert Debug.expectingException;
			Debug.expectingException = false;
		}
	}

	private static final class PrimitiveInfo<T>
	{
		final Class<T> primitiveClass;
		final Class<T> wrapperClass;

		private PrimitiveInfo( Class<T> primitiveClass, Class<T> wrapperClass )
		{
			this.primitiveClass = primitiveClass;
			this.wrapperClass = wrapperClass;
		}
	}

	private static final List<PrimitiveInfo<?>> primitiveTypeInfo = List.of( //
		new PrimitiveInfo<>( boolean.class /**/, Boolean.class ), //
		new PrimitiveInfo<>( char.class    /**/, Character.class ), //
		new PrimitiveInfo<>( byte.class    /**/, Byte.class ), //
		new PrimitiveInfo<>( short.class   /**/, Short.class ), //
		new PrimitiveInfo<>( int.class     /**/, Integer.class ), //
		new PrimitiveInfo<>( long.class    /**/, Long.class ), //
		new PrimitiveInfo<>( float.class   /**/, Float.class ), //
		new PrimitiveInfo<>( double.class  /**/, Double.class ), //
		new PrimitiveInfo<>( void.class    /**/, Void.class ) );

	/**
	 * Gets all java primitive types.
	 */
	public static Collection<Class<?>> getAllPrimitives()
	{
		return primitiveTypeInfo.stream().<Class<?>>map( i -> i.primitiveClass ).toList();
	}

	/**
	 * Gets all java primitive wrappers.
	 */
	public static Collection<Class<?>> getAllPrimitiveWrappers()
	{
		return primitiveTypeInfo.stream().<Class<?>>map( i -> i.wrapperClass ).toList();
	}
}
