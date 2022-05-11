package mikenakis.immutability.internal.mykit;

import java.lang.reflect.Field;

public final class MyKit
{
	private MyKit() { }

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Assertions & Debugging

	//PEARL: this has been observed to return false even though assertions are enabled, when invoked from a static context, e.g. main()
	public static boolean areAssertionsEnabled()
	{
		boolean b = false;
		//noinspection AssertWithSideEffects
		assert b = true;
		//noinspection ConstantConditions
		return b;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Object-related and miscellaneous.

	/**
	 * Casts a {@link Class} of any type to a {@link Class} of a given type
	 *
	 * @param jvmClass the {@link Class} to cast
	 * @param <T>      the type of the {@link Class}
	 *
	 * @return the {@link Class} cast to the requested type.
	 */
	public static <T> Class<T> uncheckedClassCast( @SuppressWarnings( "rawtypes" ) Class jvmClass )
	{
		@SuppressWarnings( "unchecked" ) Class<T> result = (Class<T>)jvmClass;
		return result;
	}

	/**
	 * Gets the class of an object as a generic class parametrized with the type of that object.
	 * <p>
	 * Useful because {@link Object#getClass()} returns a generic class parametrized with a wildcard, not with the actual type of the object on which getClass()
	 * was called.
	 *
	 * @param object the object whose class is to be obtained.
	 * @param <T>    the type of the object.
	 *
	 * @return the class of the object parametrized with the type of the object.
	 */
	public static <T> Class<T> getClass( T object )
	{
		return uncheckedClassCast( object.getClass() );
	}

	/**
	 * Obtains a string of the form type-name@hex-number which "for the most part" uniquely identifies an object. NOTE: there are no guarantees that the
	 * returned string will be truly unique.
	 *
	 * @param object the {@link Object} whose identity string is requested.
	 *
	 * @return the identity string of the object.
	 */
	public static String identityString( Object object )
	{
		if( object == null )
			return "null";
		int identityHashCode = System.identityHashCode( object );
		return getClassName( object.getClass() ) + "@" + Integer.toHexString( identityHashCode );
	}

	public static String getClassName( Class<?> jvmClass )
	{
		String text = jvmClass.getCanonicalName();
		if( text == null )
			return jvmClass.getName();
		return text;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Reflection stuff

	public static Object getFieldValue( Object object, Field field )
	{
		if( !field.canAccess( object ) ) //TODO: assess whether performing this check saves any time (as opposed to always invoking setAccessible without the check.)
			field.setAccessible( true );
		Object fieldValue;
		try
		{
			fieldValue = field.get( object );
		}
		catch( IllegalAccessException e )
		{
			throw new RuntimeException( e );
		}
		return fieldValue;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// StringBuilder stuff

	public static void appendEscapedForJava( StringBuilder stringBuilder, String s, char quote )
	{
		if( s == null )
		{
			stringBuilder.append( "null" );
			return;
		}
		stringBuilder.append( quote );
		for( char c : s.toCharArray() )
		{
			if( c == '"' )
				stringBuilder.append( "\\\"" );
			else if( c == '\r' )
				stringBuilder.append( "\\r" );
			else if( c == '\n' )
				stringBuilder.append( "\\n" );
			else if( c == '\t' )
				stringBuilder.append( "\\t" );
			else if( c < 32 )
				stringBuilder.append( String.format( "\\x%02x", (int)c ) );
			else if( !Character.isDefined( c ) )
				stringBuilder.append( String.format( "\\u%04x", (int)c ) );
			else
				stringBuilder.append( c );
		}
		stringBuilder.append( quote );
	}

	/**
	 * Appends the string representation of an {@link Object} to a {@link StringBuilder}. The difference between this function and {@link
	 * StringBuilder#append(Object)} is that this function treats {@link String} and {@link Character} differently: they are escaped and surrounded with
	 * quotes.
	 *
	 * @param stringBuilder the StringBuilder to append to.
	 * @param object        the object whose string representation is to be appended to the StringBuilder.
	 */
	public static void append( StringBuilder stringBuilder, Object object )
	{
		switch( object )
		{
			case String s -> appendEscapedForJava( stringBuilder, s, '"' );
			case Character c -> appendEscapedForJava( stringBuilder, String.valueOf( c ), '\'' );
			default -> stringBuilder.append( object );
		}
	}
	/**
	 * Gets the string representation of an {@link Object}.
	 *
	 * @param object the object whose string representation is requested.
	 */
	public static String stringFromObject( Object object )
	{
		StringBuilder stringBuilder = new StringBuilder();
		append( stringBuilder, object );
		return stringBuilder.toString();
	}
}
