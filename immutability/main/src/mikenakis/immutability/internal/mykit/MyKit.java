package mikenakis.immutability.internal.mykit;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import mikenakis.debug.Debug;
import mikenakis.immutability.internal.mykit.functional.Function0;
import mikenakis.immutability.internal.mykit.functional.Function1;
import mikenakis.immutability.internal.mykit.functional.Procedure0;
import mikenakis.immutability.internal.mykit.functional.Procedure1;

import java.util.Iterator;

@SuppressWarnings( { "unused", "NewClassNamingConvention" } )
public final class MyKit
{
	public static final byte[] ARRAY_OF_ZERO_BYTES = new byte[0];
	public static final Object[] ARRAY_OF_ZERO_OBJECTS = new Object[0];
	public static final String[] ARRAY_OF_ZERO_STRINGS = new String[0];

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

	public static boolean debugging()
	{
		if( Debug.expectingException )
			return false;
		return areAssertionsEnabled();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Object-related and miscellaneous.

	@SuppressWarnings( "CanBeFinal" ) private static final String neverNull = "";

	/**
	 * Gets the value passed as a parameter.
	 * <p>
	 * Useful for setting breakpoints, for avoiding warnings such as 'unused parameter', 'condition is always true', 'result of method call ignored', etc.
	 *
	 * @param value the value to return.
	 *
	 * @return the same value.
	 */
	@CanIgnoreReturnValue public static <T> T get( T value )
	{
		//noinspection ConstantConditions
		if( neverNull == null )
		{
			assert false;
			@SuppressWarnings( "unchecked" ) T result = (T)neverNull;
			return result;
		}
		return value;
	}

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
	 * Obtains a string of the form type-name@hex-number which "for the most part" uniquely identifies an object.
	 * NOTE: there are no guarantees that the returned string will be truly unique.
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
	// StringBuilder stuff

	public static class stringBuilder
	{
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
		 * Appends the string representation of an {@link Object} to a {@link StringBuilder}.
		 * The difference between this function and {@link StringBuilder#append(Object)} is that this function treats {@link String}
		 * differently: strings are output escaped and surrounded with quotes.
		 *
		 * @param stringBuilder the StringBuilder to append to.
		 * @param object        the object whose string representation is to be appended to the StringBuilder.
		 */
		public static void append( StringBuilder stringBuilder, Object object )
		{
			String s = string.of( object );
			stringBuilder.append( s );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// String stuff

	public static class string
	{
		public static String of( Object object )
		{
			if( object == null )
				return "null";
			if( object instanceof String s )
				return escapeForJava( s );
			return object.toString();
		}

		public static String escapeForJava( String s )
		{
			var builder = new StringBuilder();
			stringBuilder.appendEscapedForJava( builder, s, '"' );
			return builder.toString();
		}

		/**
		 * Gets the string representation of an {@link Object}.
		 * see {@link stringBuilder#append(StringBuilder, Object)}
		 *
		 * @param object the object whose string representation is requested.
		 */
		public static String from( Object object )
		{
			StringBuilder stringBuilder = new StringBuilder();
			//noinspection UnnecessarilyQualifiedInnerClassAccess
			MyKit.stringBuilder.append( stringBuilder, object );
			return stringBuilder.toString();
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Synchronization

	public static final class sync
	{
		public static void synchronize( Object lock, Procedure0 procedure )
		{
			//noinspection SynchronizationOnLocalVariableOrMethodParameter
			synchronized( lock )
			{
				Debug.boundary( () -> procedure.invoke() );
			}
		}

		public static <T> T synchronize( Object lock, Function0<T> function )
		{
			//noinspection SynchronizationOnLocalVariableOrMethodParameter
			synchronized( lock )
			{
				return Debug.boundary( () -> function.invoke() );
			}
		}
	}

	private static final String midLeaf = "├─";
	private static final String endLeaf = "└─";
	private static final String midNode = "│ ";
	private static final String endNode = "  ";
	private static final String terminal = "■ ";

	public static <T> void tree( T rootNode, Function1<Iterable<? extends T>,T> breeder, Function1<String,T> stringizer, Procedure1<String> emitter )
	{
		StringBuilder stringBuilder = new StringBuilder();
		printTreeRecursive( stringBuilder, "", rootNode, "", breeder, stringizer, emitter );
	}

	private static <T> void printTreeRecursive( StringBuilder stringBuilder, String parentPrefix, T node, String childPrefix, //
		Function1<Iterable<? extends T>,T> breeder, Function1<String,T> stringizer, Procedure1<String> emitter )
	{
		int position = stringBuilder.length();
		stringBuilder.append( parentPrefix ).append( terminal );
		stringBuilder.append( stringizer.invoke( node ) );
		emitter.invoke( stringBuilder.toString() );
		stringBuilder.setLength( position );
		stringBuilder.append( childPrefix );
		Iterator<? extends T> iterator = breeder.invoke( node ).iterator();
		while( iterator.hasNext() )
		{
			T childNode = iterator.next();
			boolean mid = iterator.hasNext();
			printTreeRecursive( stringBuilder, mid ? midLeaf : endLeaf, childNode, mid ? midNode : endNode, breeder, stringizer, emitter );
		}
		stringBuilder.setLength( position );
	}
}
