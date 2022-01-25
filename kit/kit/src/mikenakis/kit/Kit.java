package mikenakis.kit;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.kit.collections.ConvertingAndFilteringIterable;
import mikenakis.kit.collections.ConvertingIterable;
import mikenakis.kit.collections.FilteringIterable;
import mikenakis.kit.collections.UnmodifiableIterable;
import mikenakis.kit.collections.UnmodifiableIterator;
import mikenakis.kit.functional.BooleanFunction1;
import mikenakis.kit.functional.BooleanFunction1Double;
import mikenakis.kit.functional.Function0;
import mikenakis.kit.functional.Function1;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.functional.Procedure1;
import mikenakis.kit.functional.ThrowingFunction0;
import mikenakis.kit.functional.ThrowingFunction1;
import mikenakis.kit.functional.ThrowingProcedure0;
import mikenakis.kit.functional.ThrowingProcedure1;
import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.logging.Log;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@SuppressWarnings( { "unused", "NewClassNamingConvention" } )
public final class Kit
{
	public static final byte[] ARRAY_OF_ZERO_BYTES = new byte[0];
	public static final Object[] ARRAY_OF_ZERO_OBJECTS = new Object[0];
	public static final String[] ARRAY_OF_ZERO_STRINGS = new String[0];

	private Kit() { }

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Assertions

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
	 * Performs no action.
	 */
	@SuppressWarnings( "EmptyMethod" ) public static void nop()
	{
	}

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
	 * Functional assertion failure; for when you want to throw, but the compiler expects you to return something.
	 *
	 * @param <T> the type of value to return
	 *
	 * @return does not return.
	 */
	@ExcludeFromJacocoGeneratedReport public static <T> T fail()
	{
		assert false;
		return null;
	}

	/**
	 * Up-casts the generic parameter of an {@link Optional}.
	 *
	 * @param source the {@link Optional} to up-cast.
	 * @param <T>    the type to up-cast to.
	 * @param <S>    the source type.
	 *
	 * @return the same {@link Optional}, where its generic parameter is cast to the requested type.
	 */
	public static <T extends S, S> Optional<T> upCast( Optional<S> source )
	{
		@SuppressWarnings( "unchecked" ) Optional<T> result = (Optional<T>)source;
		return result;
	}

	public static <S, T extends S> Optional<S> downCast( Optional<T> optional )
	{
		@SuppressWarnings( "unchecked" ) Optional<S> result = (Optional<S>)optional;
		return result;
	}

	/**
	 * Up-casts a given value to a more derived type.
	 *
	 * @param source the value to up-cast.
	 * @param <T>    the type to up-cast to.
	 * @param <S>    the source type.
	 *
	 * @return the same value, up-cast to the requested type.
	 */
	public static <T extends S, S> T upCast( S source )
	{
		@SuppressWarnings( "unchecked" ) T result = (T)source;
		return result;
	}

	//	public static <T, D extends T> D upCast( T object )
	//	{
	//		@SuppressWarnings( "unchecked" ) D result = (D)object;
	//		return result;
	//	}

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
		int hashCode = System.identityHashCode( object );  //note: the default Object.toString() invokes own hashCode() instead of System.identityHashCode() !
		return object.getClass().getName() + "@" + Integer.toHexString( hashCode );
	}

	/**
	 * Note: the documentation of Class.newInstance() gives the following justification for its deprecation:
	 * <p>
	 * "This method propagates any exception thrown by the nullary constructor, including a checked exception.
	 * Use of this method effectively bypasses the compile-time exception checking that would otherwise be performed by the compiler."
	 * <p>
	 * Yes, yes, that is precisely what we want.
	 */
	@SuppressWarnings( "deprecation" ) public static Object newInstance( Class<?> javaClass )
	{
		return unchecked( () -> javaClass.newInstance() );
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Stacks, Stack Traces, Source code locations

	private static final StackWalker stackWalker = StackWalker.getInstance( EnumSet.noneOf( StackWalker.Option.class ) );

	public static StackWalker.StackFrame getStackFrame( int numberOfFramesToSkip )
	{
		assert numberOfFramesToSkip > 0;
		return stackWalker.walk( s -> s.skip( numberOfFramesToSkip + 1 ).limit( 1 ).reduce( null, ( a, b ) -> b ) ); //TODO: simplify
	}

	public static StackWalker.StackFrame[] getStackTrace( int numberOfFramesToSkip )
	{
		assert numberOfFramesToSkip > 0;
		return stackWalker.walk( s -> s.skip( numberOfFramesToSkip + 1 ).toArray( value -> new StackWalker.StackFrame[value] ) ); //TODO: simplify
	}

	public static SourceLocation getSourceLocation( int numberOfFramesToSkip )
	{
		StackWalker.StackFrame stackFrame = getStackFrame( numberOfFramesToSkip + 1 );
		SourceLocation sourceLocation = SourceLocation.fromStackFrame( stackFrame );
		assert sourceLocation.stringRepresentation().equals( stackFrame.toString() );
		return sourceLocation;
	}

	public static String getMethodName()
	{
		return getMethodName( 1 );
	}

	public static String getMethodName( int framesToSkip )
	{
		StackWalker.StackFrame stackFrame = getStackFrame( framesToSkip + 1 );
		return stackFrame.getMethodName();
	}

	public static String stringFromThrowable( Throwable throwable )
	{
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter( stringWriter );
		throwable.printStackTrace( printWriter );
		return stringWriter.toString();
	}

	public static boolean assertWeakly( boolean value, Function0<String> messageBuilder )
	{
		if( areAssertionsEnabled() && !value )
		{
			String message = messageBuilder.invoke();
			Log.message( Log.Level.ERROR, 1, "Assertion failure" + (message.isEmpty() ? "" : ": " + message) );
			return false;
		}
		return true;
	}

	public static boolean assertWeakly( boolean value )
	{
		return assertWeakly( value, () -> "" );
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Bytes stuff

	public static class bytes
	{
		private static final boolean isBigEndian = Function0.invoke( () -> //
		{
			ByteOrder nativeByteOrder = ByteOrder.nativeOrder();
			assert nativeByteOrder == ByteOrder.BIG_ENDIAN || nativeByteOrder == ByteOrder.LITTLE_ENDIAN;
			return nativeByteOrder == ByteOrder.BIG_ENDIAN;
		} );

		public static int compare( byte[] aBytes, int aOffset, byte[] bBytes, int bOffset, int length )
		{
			//return Arrays.compare( aBytes, aOffset, aOffset + length, bBytes, bOffset, bOffset + length );
			for( int i = 0; i < length; i++ )
			{
				int difference = aBytes[aOffset + i] - bBytes[bOffset + i];
				if( difference != 0 )
					return difference;
			}
			return 0;
		}

		public static int compare( byte[] aBytes, byte[] bBytes )
		{
			int commonLength = Math.min( aBytes.length, bBytes.length );
			int difference = compare( aBytes, 0, bBytes, 0, commonLength );
			if( difference != 0 )
				return difference;
			return Integer.compare( aBytes.length, bBytes.length );
		}

		public static int indexOf( byte[] data, int offset, int length, byte byteToFind )
		{
			assert offset >= 0;
			assert offset < data.length;
			assert length >= 0;
			int end = offset + length;
			for( int i = offset; i < end; i++ )
				if( data[i] == byteToFind )
					return i;
			return -1;
		}

		public static int lastIndexOf( byte[] data, int offset, int length, byte byteToFind )
		{
			assert offset >= 0;
			assert offset < data.length;
			assert length >= 0;
			int end = offset + length;
			for( int i = end - 1; i >= offset; i-- )
				if( data[i] == byteToFind )
					return i;
			return -1;
		}

		public static int indexOfAnyOf( byte[] data, int offset, int length, byte[] bytes )
		{
			assert offset >= 0;
			assert offset < data.length;
			assert length >= 0;
			int end = offset + length;
			for( int i = offset; i < end; i++ )
			{
				byte b = data[i];
				if( indexOf( bytes, 0, bytes.length, b ) != -1 )
					return i;
			}
			return -1;
		}

		public static int indexOf( byte[] data, int offset, int length, byte[] pattern )
		{
			assert offset >= 0;
			assert offset <= data.length;
			assert length >= 0;
			if( length < pattern.length )
				return -1;
			for( ; ; )
			{
				int i = indexOf( data, offset, length - pattern.length + 1, pattern[0] );
				if( i == -1 )
					break;
				if( compare( data, i, pattern, 0, pattern.length ) == 0 )
					return i;
				int n = i - offset + 1;
				offset += n;
				length -= n;
			}
			return -1;
		}

		public static int lastIndexOf( byte[] data, int offset, int length, byte[] pattern )
		{
			assert offset >= 0;
			assert offset < data.length;
			assert length >= 0;
			if( length < pattern.length )
				return -1;
			for( ; ; )
			{
				int i = lastIndexOf( data, offset, length - pattern.length + 1, pattern[0] );
				if( i == -1 )
					break;
				if( compare( data, i, pattern, 0, pattern.length ) == 0 )
					return i;
				int n = i - offset + 1;
				offset -= n;
				length -= n;
			}
			return -1;
		}

		public static int indexOfAnyOf( byte[] data, int offset, int length, byte[][] patterns )
		{
			assert offset >= 0;
			assert offset < data.length;
			assert length >= 0;
			byte[] firstBytes = new byte[patterns.length];
			for( int i = 0; i < firstBytes.length; i++ )
				firstBytes[i] = patterns[i][0]; //TODO: eliminate duplicates
			for( ; ; )
			{
				int i = indexOfAnyOf( data, offset, length, firstBytes );
				if( i == -1 )
					break;
				for( byte[] pattern : patterns )
				{
					if( i + pattern.length > offset + length )
						continue;
					if( compare( data, i, pattern, 0, pattern.length ) == 0 )
						return i;
				}
				int n = i - offset;
				offset += n;
				length -= n;
			}
			return -1;
		}

		@SuppressWarnings( "SpellCheckingInspection" ) private static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();

		public static String hexString( byte[] bytes )
		{
			if( bytes == null )
				return null;
			char[] hexChars = new char[bytes.length * 2];
			for( int j = 0; j < bytes.length; j++ )
			{
				int v = bytes[j] & 0xFF;
				hexChars[j * 2] = HEX_DIGITS[v >>> 4];
				hexChars[j * 2 + 1] = HEX_DIGITS[v & 0x0F];
			}
			return new String( hexChars );
		}

		public static boolean isFirstIdentifier( byte b )
		{
			return b == '_' || (b >= 'a' && b <= 'z') || (b >= 'A' && b <= 'Z');
		}

		public static boolean isNonFirstIdentifier( byte b )
		{
			if( isFirstIdentifier( b ) )
				return true;
			return b >= '0' && b <= '9';
		}

		public static boolean isWhitespace( byte b )
		{
			return switch( b )
				{
					case ' ', '\t', '\n', '\r' -> true;
					default -> false;
				};
		}

		public static class ArgumentsException extends UncheckedException
		{
			public final byte[] buffer;
			public final int offset;
			public final int count;

			ArgumentsException( byte[] buffer, int offset, int count )
			{
				this.buffer = buffer;
				this.offset = offset;
				this.count = count;
			}
		}

		public static class NegativeOffsetException extends ArgumentsException
		{
			public NegativeOffsetException( byte[] buffer, int index, int count )
			{
				super( buffer, index, count );
			}
		}

		public static class NonPositiveCountException extends ArgumentsException
		{
			public NonPositiveCountException( byte[] buffer, int index, int count )
			{
				super( buffer, index, count );
			}
		}

		public static class OffsetOutOfRangeException extends ArgumentsException
		{
			public OffsetOutOfRangeException( byte[] buffer, int index, int count )
			{
				super( buffer, index, count );
			}
		}

		public static class OffsetPlusCountOutOfRangeException extends ArgumentsException
		{
			public OffsetPlusCountOutOfRangeException( byte[] buffer, int index, int count )
			{
				super( buffer, index, count );
			}
		}

		public static Optional<ArgumentsException> validateArguments( byte[] bytes, int offset, int count )
		{
			if( !(count > 0) )
				return Optional.of( new NonPositiveCountException( bytes, offset, count ) );
			if( !(offset >= 0) )
				return Optional.of( new NegativeOffsetException( bytes, offset, count ) );
			if( !(offset < bytes.length) )
				return Optional.of( new OffsetOutOfRangeException( bytes, offset, count ) );
			if( !(offset + count <= bytes.length) )
				return Optional.of( new OffsetPlusCountOutOfRangeException( bytes, offset, count ) );
			return Optional.empty();
		}

		public static boolean validArgumentsAssertion( byte[] bytes, int offset, int count )
		{
			validateArguments( bytes, offset, count ).ifPresent( e -> { throw e; } );
			return true;
		}

		public static char charFromBytes( byte[] bytes )
		{
			return (char)shortFromBytes( bytes );
		}

		public static byte[] bytesFromChar( char value )
		{
			return bytesFromShort( (short)value );
		}

		public static short shortFromBytes( byte[] bytes )
		{
			assert bytes.length == 2;
			if( isBigEndian )
			{
				return (short)((((int)bytes[0]) & 0xFF) + (((int)bytes[1]) & 0xFF) << 8);
			}
			else
			{
				return (short)(((((int)bytes[0]) & 0xFF) << 8) + (((int)bytes[1]) & 0xFF));
			}
		}

		public static byte[] bytesFromShort( short value )
		{
			byte[] bytes = new byte[2];
			if( isBigEndian )
			{
				bytes[0] = (byte)(value & 0xFF);
				bytes[1] = (byte)((value >>> 8) & 0xFF);
			}
			else
			{
				bytes[0] = (byte)((value >>> 8) & 0xFF);
				bytes[1] = (byte)(value & 0xFF);
			}
			return bytes;
		}

		public static int intFromBytes( byte[] bytes )
		{
			assert bytes.length == 4;
			if( isBigEndian )
			{
				return ((int)bytes[0]) + ((int)bytes[1] << 8) + ((int)bytes[2] << 16) + ((int)bytes[3] << 24);
			}
			else
			{
				return ((((int)bytes[0]) & 0xFF) << 24) + ((((int)bytes[1]) & 0xFF) << 16) + ((((int)bytes[2]) & 0xFF) << 8) + (((int)bytes[3]) & 0xFF);
			}
		}

		public static byte[] bytesFromInt( int value )
		{
			byte[] bytes = new byte[4];
			if( isBigEndian )
			{
				bytes[0] = (byte)(value & 0xFF);
				bytes[1] = (byte)((value >>> 8) & 0xFF);
				bytes[2] = (byte)((value >>> 16) & 0xFF);
				bytes[3] = (byte)((value >>> 24) & 0xFF);
			}
			else
			{
				bytes[0] = (byte)((value >>> 24) & 0xFF);
				bytes[1] = (byte)((value >>> 16) & 0xFF);
				bytes[2] = (byte)((value >>> 8) & 0xFF);
				bytes[3] = (byte)(value & 0xFF);
			}
			return bytes;
		}

		public static Long longFromBytes( byte[] bytes )
		{
			assert bytes.length == 8;
			if( isBigEndian )
			{
				return ((long)bytes[0]) + ((long)bytes[1] << 8) + ((long)bytes[2] << 16) + ((long)bytes[3] << 24) + ((long)bytes[4] << 32) + ((long)bytes[5] << 40) + ((long)bytes[6] << 48) + ((long)bytes[7] << 56);
			}
			else
			{
				return ((((long)bytes[0]) & 0xFF) << 56) + ((((long)bytes[1]) & 0xFF) << 48) + ((((long)bytes[2]) & 0xFF) << 40) + ((((long)bytes[3]) & 0xFF) << 32) + ((((long)bytes[4]) & 0xFF) << 24) + ((((long)bytes[5]) & 0xFF) << 16) + ((((long)bytes[6]) & 0xFF) << 8) + (((long)bytes[7]) & 0xFF);
			}
		}

		public static byte[] bytesFromLong( long value )
		{
			byte[] bytes = new byte[8];
			if( isBigEndian )
			{
				bytes[0] = (byte)(value & 0xFF);
				bytes[1] = (byte)((value >>> 8) & 0xFF);
				bytes[2] = (byte)((value >>> 16) & 0xFF);
				bytes[3] = (byte)((value >>> 24) & 0xFF);
				bytes[4] = (byte)((value >>> 32) & 0xFF);
				bytes[5] = (byte)((value >>> 40) & 0xFF);
				bytes[6] = (byte)((value >>> 48) & 0xFF);
				bytes[7] = (byte)((value >>> 56) & 0xFF);
			}
			else
			{
				bytes[0] = (byte)((value >>> 56) & 0xFF);
				bytes[1] = (byte)((value >>> 48) & 0xFF);
				bytes[2] = (byte)((value >>> 40) & 0xFF);
				bytes[3] = (byte)((value >>> 32) & 0xFF);
				bytes[4] = (byte)((value >>> 24) & 0xFF);
				bytes[5] = (byte)((value >>> 16) & 0xFF);
				bytes[6] = (byte)((value >>> 8) & 0xFF);
				bytes[7] = (byte)(value & 0xFF);
			}
			return bytes;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// StringBuilder stuff

	public static class stringBuilder
	{
		/**
		 * Optionally appends a delimiter to a {@link StringBuilder}.
		 *
		 * @param builder   the {@link StringBuilder} to append to.
		 * @param first     a {@link boolean} indicating whether this is the first append, and therefore the delimiter should be skipped.
		 * @param delimiter the delimiter to append.
		 *
		 * @return always {@link false}, so that you can do {@code first = appendDelimiter( builder, first, delimiter )}.
		 */
		public static boolean appendDelimiter( StringBuilder builder, boolean first, char delimiter )
		{
			if( !first )
				builder.append( delimiter );
			return false;
		}

		/**
		 * Optionally appends a delimiter to a {@link StringBuilder}.
		 *
		 * @param builder   the {@link StringBuilder} to append to.
		 * @param first     a {@link boolean} indicating whether this is the first append, and therefore the delimiter should be skipped.
		 * @param delimiter the delimiter to append.
		 *
		 * @return always {@link false}, so that you can do {@code first = appendDelimiter( builder, first, delimiter )}.
		 */
		public static boolean appendDelimiter( StringBuilder builder, boolean first, String delimiter )
		{
			if( !first )
				builder.append( delimiter );
			return false;
		}

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

		public static String of( String s )
		{
			if( s == null )
				return "null";
			return escapeForJava( s );
		}

		/**
		 * Splits a string in parts using a given character as delimiter.
		 * Corrects Java's insanity of only offering versions of this function that work with regular expressions.
		 *
		 * @param stringToSplit the string to split.
		 * @param delimiter     the delimiter.
		 *
		 * @return An array of {@link String}. If the delimiter is not found, the array will contain a single element, which will be the entire source string.
		 */
		public static String[] splitAtCharacter( String stringToSplit, char delimiter )
		{
			return splitAtCharacter( stringToSplit, delimiter, false );
		}

		/**
		 * Splits a string in parts using a given character as delimiter.
		 * Optionally includes the delimiter as a separate element in the results.
		 * Corrects Java's insanity of only offering versions of this function that work with regular expressions.
		 *
		 * @param stringToSplit the string to split.
		 * @param delimiter     the delimiter.
		 *
		 * @return An array of {@link String}. If the delimiter is not found, the list will contain a single element, which will be the entire source string.
		 */
		public static String[] splitAtCharacter( String stringToSplit, char delimiter, boolean includeDelimiter )
		{
			Collection<String> result = new ArrayList<>();
			for( int position = 0; position < stringToSplit.length(); )
			{
				int i = position;
				position = stringToSplit.indexOf( delimiter, i );
				if( position == -1 )
					position = stringToSplit.length();
				else if( includeDelimiter )
					position++;
				int k = position;
				String current = stringToSplit.substring( i, k );
				result.add( current );
				if( !includeDelimiter )
					position++;
			}
			return result.toArray( String[]::new );
		}

		/**
		 * Build a string.
		 */
		public static <E> String make( String delimiter, Iterable<E> iterable )
		{
			return make( false, delimiter, iterable );
		}

		/**
		 * Build a string.
		 */
		public static <E> String make( boolean skipEmpties, String delimiter, Iterable<E> iterable )
		{
			StringBuilder stringBuilder = new StringBuilder();
			boolean first = true;
			for( E element : iterable )
			{
				String string = String.valueOf( element );
				if( string.isEmpty() && skipEmpties )
					continue;
				if( first )
					first = false;
				else
					stringBuilder.append( delimiter );
				stringBuilder.append( string );
			}
			return stringBuilder.toString();
		}

		/**
		 * Unescape a Java string.
		 * Takes a string enclosed in either in single or double quotes and possibly containing Java escape sequences, and returns the content of the string
		 * with the quotes removed and the escape sequences replaced with the actual characters that they represent.
		 *
		 * @param value a quoted string.
		 *
		 * @return the unquoted and unescaped value of the string.
		 */
		public static String unescapeForJava( String value )
		{
			if( value.length() < 2 )
				throw new RuntimeException();
			char quote = value.charAt( 0 );
			if( quote != '\'' && quote != '\"' )
				throw new RuntimeException();
			if( value.charAt( value.length() - 1 ) != quote )
				throw new RuntimeException();
			value = value.substring( 1, value.length() - 1 );
			var builder = new StringBuilder( value.length() );
			for( int i = 0; i < value.length(); i++ )
			{
				char ch = value.charAt( i );
				if( ch == '\\' )
				{
					char nextChar = (i == value.length() - 1) ? '\\' : value.charAt( i + 1 );
					if( nextChar >= '0' && nextChar <= '7' )
					{
						var code = new StringBuilder();
						code.append( nextChar );
						i++;
						if( (i < value.length() - 1) && value.charAt( i + 1 ) >= '0' && value.charAt( i + 1 ) <= '7' )
						{
							code.append( value.charAt( i + 1 ) );
							i++;
							if( (i < value.length() - 1) && value.charAt( i + 1 ) >= '0' && value.charAt( i + 1 ) <= '7' )
							{
								code.append( value.charAt( i + 1 ) );
								i++;
							}
						}
						builder.append( (char)Integer.parseInt( code.toString(), 8 ) );
						continue;
					}
					switch( nextChar )
					{
						case '\\':
							//ch = '\\';
							break;
						case 'b':
							ch = '\b';
							break;
						case 'f':
							ch = '\f';
							break;
						case 'n':
							ch = '\n';
							break;
						case 'r':
							ch = '\r';
							break;
						case 't':
							ch = '\t';
							break;
						case '\"':
							ch = '\"';
							break;
						case '\'':
							ch = '\'';
							break;
						case 'u':
							if( i >= value.length() - 5 )
							{
								ch = 'u';
								break;
							}
							String number = value.substring( i + 2, i + 6 );
							int code = Integer.parseInt( number, 16 );
							builder.append( Character.toChars( code ) );
							i += 5;
							continue;
						default:
							break;
					}
					i++;
				}
				builder.append( ch );
			}
			return builder.toString();
		}

		public static String escapeForJava( String s )
		{
			var builder = new StringBuilder();
			stringBuilder.appendEscapedForJava( builder, s, '"' );
			return builder.toString();
		}

		/**
		 * Checks whether a string is empty or consists of only whitespace.
		 * NOTE: the only characters considered as whitespace are ' ', '\t', '\n', and '\r'.
		 */
		public static boolean isEmptyOrWhitespace( String s )
		{
			for( int i = 0; i < s.length(); i++ )
				if( !isWhitespace( s.charAt( i ) ) )
					return false;
			return true;
		}

		/**
		 * Checks whether a character is whitespace.
		 * NOTE: the only characters considered as whitespace are ' ', '\t', '\n', and '\r'.
		 */
		public static boolean isWhitespace( char c )
		{
			return switch( c )
				{
					case ' ', '\t', '\n', '\r' -> true;
					default -> false;
				};
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
			Kit.stringBuilder.append( stringBuilder, object );
			return stringBuilder.toString();
		}

		/**
		 * Obtains a string consisting of a given character repeated a number of times.
		 *
		 * @param c     the character to append.
		 * @param count the number of times to append the character.
		 */
		public static String newRepeatedCharacter( char c, int count )
		{
			char[] chars = new char[count];
			for( int i = 0; i < count; i++ )
				chars[i] = c;
			return new String( chars );
		}

		public static String replaceAll( String original, String subString, String replacement )
		{
			return replaceAll( original, subString, replacement, false );
		}

		public static String replaceAllIgnoreCase( String original, String subString, String replacement )
		{
			return replaceAll( original, subString, replacement, true );
		}

		public static String replaceAll( String original, String subString, String replacement, boolean ignoreCase )
		{
			var builder = new StringBuilder();
			for( int i = 0; i < original.length(); )
			{
				int j = indexOf( original, subString, i, ignoreCase );
				if( j == -1 )
				{
					if( builder.isEmpty() )
						return original;
					builder.append( original.substring( i ) );
					break;
				}
				builder.append( original, i, j );
				i = j;
				builder.append( replacement );
				i += subString.length();
			}
			return builder.toString();
		}

		private static int indexOf( String str, String stringToFind, int fromIndex, boolean ignoreCase )
		{
			int n = str.length();
			if( n == 0 )
				return fromIndex;
			n -= stringToFind.length();
			if( n < 0 )
				return -1;
			char c = stringToFind.charAt( 0 );
			for( int i = fromIndex; i < n; i++ )
			{
				i = indexOf( str, c, i, n, ignoreCase );
				if( i == -1 )
					return -1;
				if( compare( str, i, stringToFind, 0, stringToFind.length(), ignoreCase ) == 0 )
					return i;
			}
			return -1;
		}

		private static int indexOf( String str, char charToFind, int fromIndex, int toIndex, boolean ignoreCase )
		{
			if( ignoreCase )
				charToFind = Character.toUpperCase( charToFind );
			for( int i = fromIndex; i < toIndex; i++ )
			{
				char c = str.charAt( i );
				if( ignoreCase )
					c = Character.toUpperCase( c );
				if( c == charToFind )
					return i;
			}
			return -1;
		}

		private static int compare( String str1, int start1, String str2, int start2, int length, boolean ignoreCase )
		{
			while( length > 0 )
			{
				char c1 = str1.charAt( start1++ );
				char c2 = str2.charAt( start2++ );
				if( ignoreCase )
				{
					c1 = Character.toUpperCase( c1 );
					c2 = Character.toUpperCase( c2 );
				}
				int d = Character.compare( c1, c2 );
				if( d != 0 )
					return d;
				length--;
			}
			return 0;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Files and paths

	public static final class path
	{
		public static boolean isAbsoluteNormalized( Path path )
		{
			if( !path.isAbsolute() )
				return false;
			assert path.equals( path.toAbsolutePath() );
			if( !path.normalize().equals( path ) )
				return false;
			return true;
		}

		public static boolean isAbsoluteNormalizedDirectory( Path path )
		{
			if( !isAbsoluteNormalized( path ) )
				return false;
			if( !Files.isDirectory( path ) )
				return false;
			return true;
		}

		public static String getFileNameExtension( Path path )
		{
			return getFileNameExtension( path.getFileName().toString() );
		}

		public static String getFileNameExtension( String fileName )
		{
			int i = fileName.lastIndexOf( '.' );
			if( i < 0 )
				return "";
			if( i == 0 ) //to avoid treating fileNames that begin with '.' as extensions.
				return "";
			return fileName.substring( i ); //return extension including the '.'
		}

		public static Iterable<Path> enumerateSubDirectories( Path baseDirectory )
		{
			Collection<Path> pathNames = new ArrayList<>();
			unchecked( () -> Files.walkFileTree( baseDirectory, Set.of(), 1, new SimpleFileVisitor<>()
			{
				@Override public FileVisitResult visitFile( Path filePath, BasicFileAttributes attrs )
				{
					if( !attrs.isDirectory() )
						return FileVisitResult.CONTINUE;
					collection.add( pathNames, filePath );
					return FileVisitResult.CONTINUE;
				}
			} ) );
			return pathNames;
		}

		public static Iterable<Path> enumerateFiles( Path directory )
		{
			List<Path> pathNames = new ArrayList<>();
			unchecked( () -> Files.walkFileTree( directory, new SimpleFileVisitor<>()
			{
				@Override public FileVisitResult visitFile( Path pathName, BasicFileAttributes attrs )
				{
					assert pathName.startsWith( directory );
					collection.add( pathNames, pathName );
					return FileVisitResult.CONTINUE;
				}
			} ) );
			pathNames.sort( Path::compareTo );
			return pathNames;
		}

		// NOTE:  When maven is running tests, the "user.dir" system property contains the root directory of the current module being tested.
		//        When testana is running tests, it sets the "user.dir" property accordingly.
		//        Thus, when running tests either via maven or via testana, we can obtain the path to the root directory of the current module.
		// PEARL: Windows very stupidly has a notion of a "current directory", which is a mutable global variable of process-wide scope.
		//        This means that any thread can modify it, and all other threads will be affected by the modification.
		//        (And if you are in a DotNet process, any AppDomain can modify it, and all other AppDomains will be affected! So much for isolation!)
		//        Java does not exactly have such a notion, but the "user.dir" system property (which you can get and set) is effectively the same.
		public static Path getWorkingDirectory()
		{
			return Paths.get( System.getProperty( "user.dir" ) ).toAbsolutePath().normalize();
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Sorting

	public static final class sort
	{
		/**
		 * QuickSort.
		 * <p>
		 * The advantage of using this algorithm over java's built-in sorting algorithm is that this one will not throw IllegalArgumentException
		 * ("Comparison method violates its general contract!") alternatively, we can issue System.setProperty( "java.util.Arrays.useLegacyMergeSort", "true" );
		 * and then go ahead and make use of List.sort(), but modifying the state of the JVM is a lame thing to do.
		 */
		public static <T> void quickSort( List<T> values, Comparator<T> comparator )
		{
			if( values.isEmpty() )
				return;
			quickSortRecursive( values, comparator, 0, values.size() - 1 );
		}

		private static <T> void quickSortRecursive( List<T> values, Comparator<T> comparator, int low, int high )
		{
			int i = low, j = high;
			T pivot = values.get( low + (high - low) / 2 );
			while( i <= j )
			{
				while( comparator.compare( values.get( i ), pivot ) < 0 )
					i++;
				while( comparator.compare( values.get( j ), pivot ) > 0 )
					j--;
				if( i <= j )
				{
					list.swap( values, i, j );
					i++;
					j--;
				}
			}
			if( low < j )
				quickSortRecursive( values, comparator, low, j );
			if( i < high )
				quickSortRecursive( values, comparator, i, high );
		}

		/**
		 * BubbleSort.  (In case you have some extra time to spare.)
		 *
		 * @param values     a {@link List} containing the values to sort.
		 * @param comparator the {@link Comparator} to use.
		 * @param <T>        the type of the elements.
		 */
		public static <T> void bubbleSort( List<T> values, Comparator<T> comparator )
		{
			if( values.isEmpty() )
				return;
			for( int i = 0; i < values.size(); i++ )
				for( int j = i + 1; j < values.size(); j++ )
				{
					T a = values.get( i );
					T b = values.get( j );
					int d = comparator.compare( a, b );
					if( d < 0 )
						list.swap( values, i, j );
				}
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Java Iterable<T>

	public static final class iterable
	{
		public static <T> Iterable<T> of( T element )
		{
			return List.of( element );
		}

		public static <T, D extends T> Iterable<T> downCast( Iterable<D> iterable )
		{
			@SuppressWarnings( "unchecked" ) Iterable<T> result = (Iterable<T>)iterable;
			return result;
		}

		public static <T> Iterable<T> concat( Iterable<T> iterable, T element )
		{
			return concat( iterable, List.of( element ) );
		}

		@SafeVarargs @SuppressWarnings( "varargs" ) public static <T> Iterable<T> concat( Iterable<T> iterable, T... elements )
		{
			return concat( iterable, List.of( elements ) );
		}

		@SuppressWarnings( "varargs" ) @SafeVarargs public static <T> Iterable<T> concat( Iterable<T>... iterables )
		{
			return concat( List.of( iterables ) );
		}

		public static <T> Iterable<T> concat( List<Iterable<T>> iterables )
		{
			Iterable<T> iterable = iterables.get( 0 );
			for( int i = 1; i < iterables.size(); i++ )
				iterable = concat( iterable, iterables.get( i ) );
			return iterable;
		}

		public static <T> Iterable<T> concat( Iterable<T> iterable, Iterable<T> other )
		{
			return fromStream( Stream.concat( collection.stream.fromIterable( iterable ), collection.stream.fromIterable( other ) ) );
		}

		public static <T> Iterable<T> fromStream( Stream<T> stream )
		{
			return () -> stream.iterator();
		}

		public static <T> boolean trueForAll( Iterable<T> iterable, Predicate<T> predicate )
		{
			for( var element : iterable )
				if( !predicate.test( element ) )
					return false;
			return true;
		}

		public static <T> boolean trueForAny( Iterable<T> iterable, Predicate<T> predicate )
		{
			for( var element : iterable )
				if( predicate.test( element ) )
					return true;
			return false;
		}

		public static <T> boolean falseForAll( Iterable<T> iterable, Predicate<T> predicate )
		{
			for( var element : iterable )
				if( predicate.test( element ) )
					return false;
			return true;
		}

		public static <T> boolean falseForAny( Iterable<T> iterable, Predicate<T> predicate )
		{
			for( var element : iterable )
				if( !predicate.test( element ) )
					return true;
			return false;
		}

		public static <T> Iterable<T> reversed( Iterable<T> iterable )
		{
			//TODO: optimize
			return collection.stream.fromIterable( iterable ) //
				.collect( Collectors.collectingAndThen( Collectors.toList(), l -> //
				{
					Collections.reverse( l );
					return l;
				} ) );
		}

		public static <T> Iterable<T> filtered( Iterable<T> iterable, BooleanFunction1<T> filter )
		{
			return new FilteringIterable<>( iterable, filter );
		}

		public static <T, F> Iterable<T> converted( Iterable<F> iterable, Function1<T,F> converter )
		{
			return new ConvertingIterable<>( iterable, converter );
		}

		public static <T, F> Iterable<T> convertedAndFiltered( Iterable<F> iterable, Function1<Optional<T>,F> converterAndFilterer )
		{
			return new ConvertingAndFilteringIterable<>( iterable, converterAndFilterer );
		}

		public static <T> List<T> toList( Iterable<T> iterable )
		{
			List<T> result = new ArrayList<>();
			for( T element : iterable )
				result.add( element );
			return Collections.unmodifiableList( result );
			//return collection.stream.fromIterable( iterable ).collect( Collectors.toList() );
		}

		public static <T, F> Iterable<T> filteredAndCast( Iterable<F> iterable, Class<T> elementClass )
		{
			Iterable<F> filtered = filtered( iterable, e -> elementClass.isInstance( e ) );
			return converted( filtered, e -> elementClass.cast( e ) );
		}

		public static <T> Iterable<T> unmodifiable( Iterable<T> delegee )
		{
			return new UnmodifiableIterable<>( delegee );
		}

		public static <K, V, T> Map<K,V> toMap( Iterable<T> iterable, Function1<K,T> keyExtractor, Function1<V,T> valueExtractor )
		{
			return collection.stream.fromIterable( iterable ).collect( Collectors.toMap( t -> keyExtractor.invoke( t ), t -> valueExtractor.invoke( t ), Kit::dummyMergeFunction, LinkedHashMap::new ) );
		}

		public static <K, V> Map<K,V> keysToMap( Iterable<K> iterable, Function1<V,K> valueExtractor )
		{
			return toMap( iterable, k -> k, valueExtractor );
		}

		public static <K, V> Map<K,V> valuesToMap( Iterable<V> iterable, Function1<K,V> keyExtractor )
		{
			return toMap( iterable, keyExtractor, v -> v );
		}

		public static <T> String makeString( Iterable<T> iterable, String delimiter )
		{
			return collection.stream.fromIterable( iterable ).map( e -> e.toString() ).collect( Collectors.joining( delimiter ) );
		}
	}

	private static <T> T dummyMergeFunction( T a, T b )
	{
		assert false;
		return a;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Java Iterator<T>

	public static final class iterator
	{
		public static <T> Iterator<T> unmodifiable( Iterator<T> delegee )
		{
			return new UnmodifiableIterator<>( delegee );
		}

		public static <T> List<T> toList( Iterator<T> iterator )
		{
			if( !iterator.hasNext() )
				return List.of();
			List<T> list = new ArrayList<>();
			while( iterator.hasNext() )
				list.add( iterator.next() );
			return list;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Java Collection<T>

	public static final class collection
	{
		public static final class stream
		{
			/***
			 * Obtains a {@link Stream} from an {@link Iterable}.
			 * Because Java makes it awfully difficult, whereas it should have been so easy as to not even require a cast. (Ideally, Stream would extend
			 * Iterable. I know, it can't. But ideally, it would.)
			 */
			public static <T> Stream<T> fromIterable( Iterable<T> iterable )
			{
				return StreamSupport.stream( iterable.spliterator(), false );
			}
		}

		/**
		 * Casts a {@link Collection} to a {@link Collection} of an ancestral element type.
		 *
		 * @param collection the {@link Collection} to down-cast.
		 * @param <T>        the required ancestral element type.
		 *
		 * @return the same {@link Collection} where the element type is the given ancestral type.
		 */
		public static <T> Collection<T> downCast( Collection<? extends T> collection )
		{
			@SuppressWarnings( "unchecked" ) Collection<T> result = (Collection<T>)collection;
			return result;
		}

		/**
		 * Adds an item to a {@link Collection}. The item must not already exist.
		 * Corresponds to Java's {@link Collection#add(T)}, except that it corrects Java's deplorable dumbfuckery of not throwing an exception when the item
		 * already exists.
		 */
		/*@SuppressWarnings( "deprecation" )*/
		public static <T> void add( Collection<T> collection, T item )
		{
			assert item != null;
			boolean ok = collection.add( item );
			assert ok;
		}

		/**
		 * Tries to add an item to a {@link Collection}.
		 * Corresponds to Java's {@link Collection#add(T)}.
		 *
		 * @return {@code true} if the item was added; {@code false} if the item was not added because it already existed.
		 */
		/*@SuppressWarnings( "deprecation" )*/
		public static <T> boolean tryAdd( Collection<T> collection, T item )
		{
			assert !(collection instanceof List);
			return collection.add( item );
		}

		/**
		 * Adds an item in a {@link Collection} or replaces it if it already exists.
		 * Corresponds to Java's {@link Collection#add(T)}.
		 */
		/*@SuppressWarnings( "deprecation" )*/
		public static <T> void addOrReplace( Collection<T> collection, T item )
		{
			assert !(collection instanceof List);
			collection.add( item );
		}

		/**
		 * Removes an item from a {@link Collection}. The item must already exist.
		 * Corresponds to Java's {@link Collection#remove(T)}, except that it corrects Java's deplorable dumbfuckery of not throwing an exception when the item
		 * does not exist.
		 */
		/*@SuppressWarnings( "deprecation" )*/
		public static <T> void remove( Collection<T> collection, T item )
		{
			boolean ok = collection.remove( item );
			assert ok;
		}

		/**
		 * Removes an item from a {@link Collection} if it exists.
		 * Corresponds to Java's {@link Collection#remove(T)}.
		 *
		 * @return {@code true} if the item was removed; {@code false} if the item was not removed because it did not exist.
		 */
		/*@SuppressWarnings( "deprecation" )*/
		public static <T> boolean tryRemove( Collection<T> collection, T item )
		{
			return collection.remove( item );
		}

		/**
		 * Adds or removes an item from a {@link Collection}.
		 */
		public static <T> void addOrRemove( Collection<T> collection, boolean add, T item )
		{
			if( add )
				add( collection, item );
			else
				remove( collection, item );
		}

		public static <T> Collection<T> union( Collection<T> a, Collection<T> b )
		{
			Collection<T> result = new LinkedHashSet<>( a );
			result.addAll( b );
			return result;
		}

		public static <T> Collection<T> intersection( Collection<T> a, Collection<T> b )
		{
			Collection<T> result = new LinkedHashSet<>( a );
			result.retainAll( b );
			return result;
		}

		public static <T> Collection<T> difference( Collection<T> a, Collection<T> b )
		{
			Collection<T> result = new LinkedHashSet<>( a );
			result.removeAll( b );
			return result;
		}

		/*@SuppressWarnings( "deprecation" )*/
		public static <T> boolean contains( Collection<T> collection, T item )
		{
			assert item != null;
			return collection.contains( item );
		}

		public static <T> boolean containsAny( Collection<T> a, Collection<T> b )
		{
			for( T element : b )
				if( contains( a, element ) )
					return true;
			return false;
		}

		/**
		 * Returns a new {@link ArrayList} containing the elements of the given {@link Collection} in reverse order.
		 * Important note: the returned list is a new mutable {@link ArrayList}, it is not just a reverse mapping onto the original collection.
		 */
		public static <T> ArrayList<T> reversed( Collection<T> list )
		{
			ArrayList<T> newList = new ArrayList<>( list );
			Collections.reverse( newList );
			return newList;
		}

		public static <T> boolean equal( Collection<T> a, Collection<T> b )
		{
			return a.size() == b.size() && a.containsAll( b ) && b.containsAll( a );
		}

		@SuppressWarnings( "varargs" ) @SafeVarargs public static <T> Collection<T> chain( Collection<T>... collections )
		{
			return chain( List.of( collections ) );
		}

		public static <T> Collection<T> chain( Collection<Collection<T>> collections )
		{
			return new AbstractCollection<>()
			{
				@Override public Iterator<T> iterator()
				{
					return new Iterator<>()
					{
						private final Iterator<Collection<T>> ii = collections.iterator();
						private Iterator<T> i = prime( ii.hasNext() ? ii.next().iterator() : null );

						@Override public boolean hasNext()
						{
							return i != null && i.hasNext();
						}

						@Override public T next()
						{
							if( !hasNext() )
								throw new NoSuchElementException( "Chain exhausted." );
							T next = i.next();
							i = prime( i );
							return next;
						}

						private Iterator<T> prime( Iterator<T> j )
						{
							while( j != null && !j.hasNext() )
								j = ii.hasNext() ? ii.next().iterator() : null;
							return j;
						}
					};
				}
				@Override public int size()
				{
					return collections.stream().mapToInt( collection -> collection.size() ).sum();
				}
			};
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Java Map<K,V>

	public static final class map
	{
		/**
		 * Gets a value by key from a {@link Map}. The key must exist.
		 * Corresponds to Java's {@link Map#get(K)}, except that it corrects Java's deplorable dumbfuckery of not throwing an exception when the key does not
		 * exist.
		 */
		/*@SuppressWarnings( "deprecation" )*/
		public static <K, V> V get( Map<K,V> map, K key )
		{
			assert key != null;
			V value = map.get( key ); //delegation
			assert value != null;
			return value;
		}

		/**
		 * Tries to get a value by key from a {@link Map}. The key may and may not exist.
		 * Corresponds to Java's {@link Map#get(K)}, the difference being that by using this method we are documenting the fact that we are intentionally
		 * allowing the key to potentially not exist and that {@code null} may be returned.
		 */
		/*@SuppressWarnings( "deprecation" )*/
		public static <K, V> V tryGet( Map<K,V> map, K key )
		{
			assert key != null;
			return map.get( key ); //delegation
		}

		/**
		 * Tries to get a value by key from a {@link Map}. The key may and may not exist.
		 */
		public static <K, V> Optional<V> getOptional( Map<K,V> map, K key )
		{
			return Optional.ofNullable( tryGet( map, key ) );
		}

		/**
		 * Adds a key-value pair to a {@link Map}. The key must not already exist.
		 * Corresponds to Java's {@link Map#put(K, V)}, except that it corrects Java's deplorable dumbfuckery of not throwing an exception when the key already
		 * exists.
		 */
		/*@SuppressWarnings( "deprecation" )*/
		public static <K, V> void add( Map<K,V> map, K key, V value )
		{
			assert key != null;
			assert value != null;
			Object previous = map.put( key, value );
			assert previous == null : key + " " + value;
		}

		/**
		 * Removes a key-value pair from a {@link Map}. The key must already exist.
		 * Corresponds to Java's {@link Map#remove(K)}, except that it corrects Java's deplorable dumbfuckery of not throwing an exception when the key does not
		 * exist.
		 */
		/*@SuppressWarnings( "deprecation" )*/
		public static <K, V> V remove( Map<K,V> map, K key )
		{
			V previous = map.remove( key );
			assert previous != null;
			return previous;
		}

		/**
		 * Removes a key-value pair from a {@link Map}. The key must already exist and must map to the given value.
		 * Corresponds to Java's {@link Map#remove(K)}, except that it corrects Java's deplorable dumbfuckery of not throwing an exception when the key does not
		 * exist or when the key is not mapped to the expected value.
		 */
		/*@SuppressWarnings( "deprecation" )*/
		public static <K, V> void remove( Map<K,V> map, K key, V value )
		{
			V previous = remove( map, key );
			assert previous.equals( value );
		}

		/**
		 * Tries to remove a key-value pair from a {@link Map}.
		 * Corresponds to Java's {@link Map#remove(K)}, but with a name that documents exactly what it does.
		 *
		 * @return the value that was previously associated with the given key, or {@code null} if the key was not in the map.
		 */
		/*@SuppressWarnings( "deprecation" )*/
		public static <K, V> V tryRemove( Map<? extends K,V> map, K key )
		{
			return map.remove( key );
		}

		/**
		 * Removes a key from a {@link Map}, if the key is present. The key may and may not already exist.
		 * Corresponds to Java's {@link Map#remove(K)}, the difference being that by using this method we are documenting the fact that we are intentionally
		 * allowing the key to potentially not exist.
		 */
		/*@SuppressWarnings( "deprecation" )*/
		public static <K, V> void removeIfPresent( Map<K,V> map, K key )
		{
			map.remove( key );
		}

		/**
		 * Tries to add a key-value pair to a {@link Map}.
		 * Corresponds to Java's {@link Map#put(K, V)}, the difference being that by using this method we are documenting the fact that we are intentionally
		 * allowing the key to already exist, potentially.
		 *
		 * @return {@link true} if the pair was successfully added; {@link false} if the key was already present.
		 */
		/*@SuppressWarnings( "deprecation" )*/
		public static <K, V> boolean tryAdd( Map<K,V> map, K key, V value )
		{
			assert key != null;
			assert value != null;
			Object previous = map.put( key, value );
			if( previous == null )
				return true;
			assert Objects.equals( previous, value );
			return false;
		}

		/**
		 * Tries to replace the value associated with a given key.
		 * If the key already exists, then the value associated with it is replaced and {@code true} is returned.
		 * If the key does not already exist, then the map is not modified and {@code false} is returned.
		 *
		 * @param map   the map.
		 * @param key   the key.
		 * @param value the new value for the key.
		 * @param <K>   the type of the key.
		 * @param <V>   the type of the value.
		 *
		 * @return {@code true} if successful; {@code false} otherwise.
		 */
		/*@SuppressWarnings( "deprecation" )*/
		public static <K, V> boolean tryReplace( Map<K,V> map, K key, V value )
		{
			assert key != null;
			assert value != null;
			if( !map.containsKey( key ) )
				return false;
			map.put( key, value );
			return true;
		}

		/**
		 * Replaces the value associated with a given key. The key must already exist.
		 * Corresponds to Java's {@link Map#put(K, V)}, except that it corrects Java's deplorable dumbfuckery of not throwing an exception when the key does not
		 * exist.
		 */
		/*@SuppressWarnings( "deprecation" )*/
		public static <K, V> void replace( Map<K,V> map, K key, V value )
		{
			assert key != null;
			assert value != null;
			Object old = map.put( key, value );
			assert old != null;
		}

		/**
		 * Adds or replaces a key-value pair in a {@link Map}. The key may and may not already exist.
		 * Corresponds to Java's {@link Map#put}, the difference being that by using this method we are documenting the fact that we are intentionally
		 * performing this very odd and rarely used operation, allowing the key to either exist or not exist.
		 */
		/*@SuppressWarnings( "deprecation" )*/
		public static <K, V> V addOrReplace( Map<K,V> map, K key, V value )
		{
			assert key != null;
			assert value != null;
			return map.put( key, value );
		}

		public static <K, V> Stream<K> getKeys( Map<K,V> map, V value )
		{
			return map.entrySet().stream() //
				.filter( entry -> value.equals( entry.getValue() ) ) //
				.map( Map.Entry::getKey );
		}

		public static <K, V> K tryGetKey( Map<K,V> map, V value )
		{
			List<K> keys = getKeys( map, value ).toList();
			if( keys.isEmpty() )
				return null;
			assert keys.size() == 1;
			return keys.get( 0 );
		}

		public static <K, V> Optional<K> getKeyOptional( Map<K,V> map, V value )
		{
			return Optional.ofNullable( tryGetKey( map, value ) );
		}

		public static <K, V> K getKey( Map<K,V> map, V value )
		{
			K key = tryGetKey( map, value );
			assert key != null;
			return key;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Java List<T>

	public static final class list
	{
		/**
		 * Swaps two items in a {@link List}.
		 */
		public static <T> void swap( List<T> list, int i, int j )
		{
			T temp = list.get( i );
			list.set( i, list.get( j ) );
			list.set( j, temp );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Java time

	public static final class time
	{
		private static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm:ss" ).withZone( ZoneId.systemDefault() );

		/**
		 * Obtains a time coordinate in seconds, from some arbitrary origin, with as much precision as the underlying architecture allows.
		 */
		public static double timeSeconds()
		{
			//return System.currentTimeMillis() * 1e-3;
			return System.nanoTime() * 1e-9;
		}

		/**
		 * Obtains the string representation of a given {@link Instant} (presumed to be in UTC) as a local time,
		 * in a format similar to ISO 8601 but without the ISO 8601 weirdness.
		 *
		 * @param instant the {@link Instant} to convert.
		 *
		 * @return the local time represented by the instant as a ISO-8601-ish string.
		 */
		public static String localTimeString( Instant instant )
		{
			return timeFormat.format( instant );
		}

		/**
		 * Creates an instant at a specific year, month, day, hour, minute, second, and millisecond UTC.
		 * PEARL: Java makes it surprisingly difficult to create an {@link Instant} at a specific point in time. This function does it.
		 */
		public static Instant createInstant( int year, int month, int day, int hour, int minute, int second, int millisecond )
		{
			return ZonedDateTime.of( year, month, day, hour, minute, second, millisecond * 1000 * 1000, ZoneOffset.UTC ).toInstant();
		}

		public static Duration durationFromSeconds( double seconds )
		{
			long s = (long)seconds;
			long n = (long)((seconds - s) * 1e9);
			return Duration.ofSeconds( s, n );
		}

		public static double secondsFromDuration( Duration duration )
		{
			return duration.toNanos() * 1e-9;
		}

		public static double nanosecondsFromSeconds( double seconds )
		{
			return seconds * 1e9;
		}

		public static double secondsFromNanoseconds( double nanoseconds )
		{
			return nanoseconds * 1e-9;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Debugging helpers (try-catch, try-finally, etc.)

	public static void trySwallow( Procedure0 procedure0 )
	{
		try
		{
			procedure0.invoke();
		}
		catch( Throwable throwable )
		{
			Log.error( throwable );
		}
	}

	/**
	 * <p>Performs a debugger-friendly {@code try-catch} which returns a result.</p>
	 * <ul>
	 *     <li><p>If assertions <b>are not</b> enabled:
	 *         <ul>
	 *             <li><p>It opens a {@code try-catch} block.</p></li>
	 *             <li><p>In the {@code try} part, it invokes the try-function and returns its result.</p></li>
	 *             <li><p>In the {@code catch} part, it passes any caught exception to the catch-procedure.</p></li>
	 *         </ul></p></li>
	 *     <li><p>If assertions <b>are</b> enabled:
	 *         <ul>
	 *             <li><p>It invokes the try-function and returns its result, but it does so without using a {@code try-catch} block,
	 *             so that if an exception occurs, the debugger will stop at the throwing statement.</p></li>
	 *         </ul></p></li>
	 *     <li><p>As a bonus, it also avoids Java's deplorable dumbfuckery of forcing you in the case of {@code try-catch} to use curly braces
	 *     even when the code blocks consist of single statements. (You supply the {@code try} code in a lambda, so you get to decide whether to
	 *     use curly braces or not.)</p></li>
	 *
	 * @param tryFunction    the function to be invoked in the 'try' block.
	 * @param catchProcedure the procedure to handle any throwable thrown.
	 * @param <R>            the type of result returned.
	 *
	 * @return the result of the try-function if successful; {@code null} otherwise.
	 */
	public static <R> R tryCatch( Function0<R> tryFunction, Procedure1<Throwable> catchProcedure )
	{
		if( areAssertionsEnabled() )
			return tryFunction.invoke();
		else
		{
			try
			{
				return tryFunction.invoke();
			}
			catch( Throwable throwable )
			{
				catchProcedure.invoke( throwable );
				return null;
			}
		}
	}

	/**
	 * <p>Performs a debugger-friendly {@code try-catch} which does not return a result.</p>
	 * <ul>
	 *     <li><p>If assertions <b>are not</b> enabled:
	 *         <ul>
	 *             <li><p>It opens a {@code try-catch} block.</p></li>
	 *             <li><p>In the {@code try} part, it invokes the try-procedure.</p></li>
	 *             <li><p>In the {@code catch} part, it passes any caught exception to the catch-procedure.</p></li>
	 *         </ul></p></li>
	 *     <li><p>If assertions <b>are</b> enabled:
	 *         <ul>
	 *             <li><p>It invokes the try-procedure, but it does so without using a {@code try-catch} block,
	 *             so that if an exception occurs, the debugger will stop at the throwing statement.</p></li>
	 *         </ul></p></li>
	 *     <li><p>As a bonus, it also avoids Java's deplorable dumbfuckery of forcing you in the case of {@code try-catch} to use curly braces
	 *     even when the code blocks consist of single statements. (You supply the {@code try} code in a lambda, so you get to decide whether to
	 *     use curly braces or not.)</p></li>
	 *
	 * @param tryProcedure   the procedure to be invoked in the 'try' block.
	 * @param catchProcedure the handler to receive any throwable thrown.
	 */
	public static void tryCatch( Procedure0 tryProcedure, Procedure1<Throwable> catchProcedure )
	{
		if( areAssertionsEnabled() )
		{
			tryProcedure.invoke();
		}
		else
		{
			try
			{
				tryProcedure.invoke();
			}
			catch( Throwable throwable )
			{
				catchProcedure.invoke( throwable );
			}
		}
	}

	/**
	 * <p>Performs a debugger-friendly {@code try-catch} and returns any caught {@link Throwable}.</p>
	 * <ul>
	 *     <li><p>If assertions <b>are not</b> enabled:
	 *         <ul>
	 *             <li><p>It opens a {@code try-catch} block.</p></li>
	 *             <li><p>In the {@code try} part, it invokes the try-procedure and returns an empty {@link Optional}.</p></li>
	 *             <li><p>In the {@code catch} part, it returns the caught exception wrapped in an {@link Optional}.</p></li>
	 *         </ul></p></li>
	 *     <li><p>If assertions <b>are</b> enabled:
	 *         <ul>
	 *             <li><p>It invokes the try-procedure and returns an empty {@link Optional}, but it does so without using a {@code try-catch} block,
	 *             so that if an exception occurs, the debugger will stop at the throwing statement.</p></li>
	 *         </ul></p></li>
	 *     <li><p>As a bonus, it also avoids Java's deplorable dumbfuckery of forcing you in the case of {@code try-catch} to use curly braces
	 *     even when the code blocks consist of single statements. (You supply the {@code try} code in a lambda, so you get to decide whether to
	 *     use curly braces or not.)</p></li>
	 *
	 * @param tryProcedure the procedure to be invoked in the 'try' block.
	 */
	public static Optional<Throwable> tryCatch( Procedure0 tryProcedure )
	{
		if( areAssertionsEnabled() )
		{
			tryProcedure.invoke();
			return Optional.empty();
		}
		else
		{
			try
			{
				tryProcedure.invoke();
				return Optional.empty();
			}
			catch( Throwable throwable1 )
			{
				return Optional.of( throwable1 );
			}
		}
	}

	/**
	 * <p>Performs a debugger-friendly {@code try-finally} that returns a result.</p>
	 * <ul>
	 *     <li><p>If assertions <b>are not</b> enabled:
	 *         <ul>
	 *             <li><p>It opens a {@code try-finally} block.</p></li>
	 *             <li><p>In the {@code try} part, it invokes the try-function and returns its result.</p></li>
	 *             <li><p>In the {@code finally} part, it invokes the finally-procedure.</p></li>
	 *         </ul></p></li>
	 *     <li><p>If assertions <b>are</b> enabled:
	 *         <ul>
	 *             <li><p>It invokes the try-function and returns its result, but it does so without using a {@code try-catch} block,
	 *             so that if an exception occurs, the debugger will stop at the throwing statement.</p></li>
	 *             <li><p>If no exception is thrown:
	 *                  <ul>
	 *                      <li><p>It invokes the finally-procedure.</p></li>
	 *                  </ul></p></li>
	 *         </ul></p></li>
	 *     <li><p>As a bonus, it also avoids Java's deplorable dumbfuckery of forcing you to use curly braces even when the code blocks consist of single
	 *     statements. (You supply lambdas for the {@code try} block and the {@code finally} block, so you get to decide whether your lambdas will use curly
	 *     braces or not.)</p></li>
	 *
	 * @param tryFunction    the function to be invoked in the 'try' block.
	 * @param finallyHandler the handler to be invoked after the try-function.
	 * @param <R>            the type of the result.
	 *
	 * @return the result of the try-function.
	 */
	public static <R> R tryFinally( Function0<R> tryFunction, Procedure0 finallyHandler )
	{
		if( areAssertionsEnabled() )
		{
			R result = tryFunction.invoke();
			finallyHandler.invoke();
			return result;
		}
		else
		{
			try
			{
				return tryFunction.invoke();
			}
			finally
			{
				finallyHandler.invoke();
			}
		}
	}

	/**
	 * <p>Performs a debugger-friendly {@code try-finally} that does not return a result.</p>
	 * <ul>
	 *     <li><p>If assertions <b>are not</b> enabled:
	 *         <ul>
	 *             <li><p>It opens a {@code try-finally} block.</p></li>
	 *             <li><p>In the {@code try} part, it invokes the try-procedure.</p></li>
	 *             <li><p>In the {@code finally} part, it invokes the finally-procedure.</p></li>
	 *         </ul></p></li>
	 *     <li><p>If assertions <b>are</b> enabled:
	 *         <ul>
	 *             <li><p>It invokes the try-procedure, but it does so without using a {@code try-catch} block,
	 *             so that if an exception occurs, the debugger will stop at the throwing statement.</p></li>
	 *             <li><p>If no exception is thrown:
	 *                  <ul>
	 *                      <li><p>It invokes the finally-procedure.</p></li>
	 *                  </ul></p></li>
	 *         </ul></p></li>
	 *     <li><p>As a bonus, it also avoids Java's deplorable dumbfuckery of forcing you to use curly braces even when the code blocks consist of single
	 *     statements. (You supply lambdas for the {@code try} block and the {@code finally} block, so you get to decide whether your lambdas will use curly
	 *     braces or not.)</p></li>
	 * </ul>
	 *
	 * @param tryProcedure   the procedure to be invoked in the 'try' part.
	 * @param finallyHandler the handler to be invoked in the 'finally' part.
	 */
	public static void tryFinally( Procedure0 tryProcedure, Procedure0 finallyHandler )
	{
		if( areAssertionsEnabled() )
		{
			tryProcedure.invoke();
			finallyHandler.invoke();
		}
		else
		{
			try
			{
				tryProcedure.invoke();
			}
			finally
			{
				finallyHandler.invoke();
			}
		}
	}

	/**
	 * <p>Performs a debugger-friendly {@code try-with-resources} that returns a result.</p>
	 * <ul>
	 *     <li><p>If assertions <b>are not</b> enabled:
	 *         <ul>
	 *             <li><p>It opens a {@code try-finally} block.</p></li>
	 *             <li><p>In the {@code try} part, it invokes the try-function, passing it the closeable object, and returns its result.</p></li>
	 *             <li><p>In the {@code finally} part, it invokes {@link Closeable#close()} on the closeable object.</p></li>
	 *         </ul></p></li>
	 *     <li><p>If assertions <b>are</b> enabled:
	 *         <ul>
	 *             <li><p>It invokes the try-function, passing it the closeable object, but it does so without using a {@code try-catch} block,
	 *             so that if an exception occurs, the debugger will stop at the throwing statement.</p></li>
	 *             <li><p>If no exception is thrown:
	 *                  <ul>
	 *                      <li><p>It invokes {@link Closeable#close()} on the closeable object.</p></li>
	 *                      <li><p>It returns the result of invoking try-function.</p></li>
	 *                  </ul></p></li>
	 *         </ul></p></li>
	 *     <li><p>As a bonus, it also avoids the deplorable dumbfuckery of Java's {@code try-with-resources} where:
	 *         <ul>
	 *             <li><p>it forces you to use curly braces even when the code block consists of a single-statement
	 *              (You supply the {@code try} code in a lambda, so you get to decide whether to use curly braces or not)</p></li>
	 *             <li><p>it forces you to declare a variable, complete with its type, for the closeable object
	 *              (You only have to declare the parameter to the lambda, without the type.)</p></li>
	 *        </ul></p></li>
	 * </ul>
	 *
	 * @param closeable   the {@link Closeable} to close when done.
	 * @param tryFunction a function which receives the {@link Closeable} object and produces a result.
	 * @param <C>         the type of the {@link Closeable}. (Must extend {@link Closeable}.)
	 * @param <R>         the type of the result.
	 *
	 * @return the result of the try-function.
	 */
	public static <C extends Closeable, R> R tryGetWith( C closeable, Function1<R,? super C> tryFunction )
	{
		assert closeable != null;
		if( areAssertionsEnabled() )
		{
			R result = tryFunction.invoke( closeable );
			closeable.close();
			return result;
		}
		else
		{
			try( closeable )
			{
				return tryFunction.invoke( closeable );
			}
		}
	}

	/**
	 * Same as {@link #tryGetWith(C, Function1)} but with a {@link Function0}, for situations where we want to create a {@link Closeable}, execute some code,
	 * and then destroy the {@link Closeable} but the code does not actually need to use the {@link Closeable}.
	 * <p>
	 * As an added bonus, avoids Java's deplorable dumbfuckery of forcing you to declare the type of the variable for the closeable.
	 *
	 * @param closeable   the {@link Closeable} to close when done.
	 * @param tryFunction a function which produces a result.
	 * @param <C>         the type of the {@link Closeable}. (Must extend {@link Closeable}.)
	 * @param <R>         the type of the result.
	 *
	 * @return the result of the try-function.
	 */
	public static <C extends Closeable, R> R tryGetWith( C closeable, Function0<R> tryFunction )
	{
		assert closeable != null;
		if( areAssertionsEnabled() )
		{
			R result = tryFunction.invoke();
			closeable.close();
			return result;
		}
		else
		{
			try( closeable )
			{
				return tryFunction.invoke();
			}
		}
	}

	/**
	 * <p>Performs a debugger-friendly {@code try-with-resources} that does not return a result.</p>
	 * <ul>
	 *     <li><p>If assertions <b>are not</b> enabled:
	 *         <ul>
	 *             <li><p>It opens a {@code try-finally} block.</p></li>
	 *             <li><p>In the {@code try} part, it invokes the try-procedure, passing it the closeable object.</p></li>
	 *             <li><p>In the {@code finally} part, it invokes {@link Closeable#close()} on the closeable object.</p></li>
	 *         </ul></p></li>
	 *     <li><p>If assertions <b>are</b> enabled:
	 *         <ul>
	 *             <li><p>It invokes the try-procedure, passing it the closeable object, but it does so without using a {@code try-catch} block,
	 *             so that if an exception occurs, the debugger will stop at the throwing statement.</p></li>
	 *             <li><p>If no exception is thrown:
	 *                  <ul>
	 *                      <li><p>It invokes {@link Closeable#close()} on the closeable object.</p></li>
	 *                  </ul></p></li>
	 *         </ul></p></li>
	 *     <li><p>As a bonus, it also avoids the deplorable dumbfuckery of Java's {@code try-with-resources} where:
	 *         <ul>
	 *             <li><p>it forces you to use curly braces even when the code block consists of a single-statement
	 *              (You supply the {@code try} code in a lambda, so you get to decide whether to use curly braces or not)</p></li>
	 *             <li><p>it forces you to declare a variable, complete with its type, for the closeable object
	 *              (You only have to declare the parameter to the lambda, without the type.)</p></li>
	 *        </ul></p></li>
	 * </ul>
	 *
	 * @param closeable    the {@link Closeable} to close when done.
	 * @param tryProcedure a {@link Procedure1} which receives the closeable object and does something with it.
	 * @param <C>          the type of the {@link Closeable}. (Must extend {@link Closeable}.)
	 */
	public static <C extends Closeable> void tryWith( C closeable, Procedure1<? super C> tryProcedure )
	{
		if( areAssertionsEnabled() )
		{
			tryProcedure.invoke( closeable );
			closeable.close();
		}
		else
		{
			try( closeable )
			{
				tryProcedure.invoke( closeable );
			}
		}
	}

	/**
	 * Same as {@link #tryWith(C, Procedure1)} but with a {@link Procedure0}, for situations where we want to create a {@link Closeable}, execute some code,
	 * and then destroy the {@link Closeable} but the code does not actually need to use the {@link Closeable}.
	 * <p>
	 * As an added bonus, avoids Java's deplorable dumbfuckery of forcing you to declare the type of the variable for the closeable.
	 *
	 * @param closeable    the {@link Closeable} to close when done.
	 * @param tryProcedure the {@link Procedure0} to execute.
	 * @param <C>          the type of the {@link Closeable}. (Must extend {@link Closeable}.)
	 */
	public static <C extends Closeable> void tryWith( C closeable, Procedure0 tryProcedure )
	{
		if( areAssertionsEnabled() )
		{
			tryProcedure.invoke();
			closeable.close();
		}
		else
		{
			try( closeable )
			{
				tryProcedure.invoke();
			}
		}
	}

	/**
	 * <p>Invokes a given {@link ThrowingFunction0} and returns the result, converting the checked exception to unchecked.</p>
	 * <p>The conversion occurs at compilation time, so that:
	 * <ul>
	 *     <li>It does not incur any runtime overhead.</li>
	 *     <li>In the event that an exception is thrown, it does not prevent the debugger from stopping at the throwing statement.</li>
	 * </ul></p>
	 *
	 * @param throwingFunction the {@link ThrowingFunction0} to invoke.
	 * @param <E>              the type of checked exception declared by the {@link ThrowingFunction0}.
	 * @param <R>              the type of the result.
	 */
	public static <R, E extends Exception> R unchecked( ThrowingFunction0<R,E> throwingFunction )
	{
		@SuppressWarnings( "unchecked" ) ThrowingFunction0<R,RuntimeException> f = (ThrowingFunction0<R,RuntimeException>)throwingFunction;
		return f.invoke();
	}

	/**
	 * <p>Invokes a given {@link ThrowingProcedure0} converting the checked exception to unchecked.</p>
	 * <p>The conversion occurs at compilation time, so that:
	 * <ul>
	 *     <li>It does not incur any runtime overhead.</li>
	 *     <li>In the event that an exception is thrown, it does not prevent the debugger from stopping at the throwing statement.</li>
	 * </ul></p>
	 *
	 * @param throwingProcedure the {@link ThrowingProcedure0} to invoke.
	 * @param <E>               the type of checked exception declared by the {@link ThrowingProcedure0}.
	 */
	public static <E extends Exception> void unchecked( ThrowingProcedure0<E> throwingProcedure )
	{
		@SuppressWarnings( "unchecked" ) ThrowingProcedure0<RuntimeException> p = (ThrowingProcedure0<RuntimeException>)throwingProcedure;
		p.invoke();
	}

	public interface ThrowableThrowingFunction<R, E extends Throwable>
	{
		R invoke() throws E;
	}

	/**
	 * Invokes a given function declared with {@code throws Throwable}.
	 * <p>
	 * PEARL: just to keep things interesting, Java does not only support checked exceptions, it even allows a method to be declared with
	 * {@code throws Throwable}, in which case the caller is forced to somehow do something about the declared {@code Throwable}, despite the fact
	 * that {@link Throwable} is not a checked exception.
	 * And sure enough, the JDK makes use of this weird feature in at least a few places that I am aware of, e.g. in {@code MethodHandle.invoke()}
	 * and {@code MethodHandle.invokeExact()}.
	 * The following method allows us to invoke methods declared with {@code throws Throwable} without having to handle or in any other way deal
	 * with the {@code Throwable}.
	 *
	 * @param function the {@link ThrowableThrowingFunction} to invoke.
	 * @param <R>      the type of result returned by the function.
	 * @param <E>      the type of throwable declared by the {@link ThrowableThrowingFunction}.
	 *
	 * @return the result of the function.
	 */
	public static <R, E extends Throwable> R invokeThrowableThrowingFunction( ThrowableThrowingFunction<R,E> function )
	{
		@SuppressWarnings( "unchecked" ) ThrowableThrowingFunction<R,RuntimeException> f = (ThrowableThrowingFunction<R,RuntimeException>)function;
		return f.invoke();
	}

	/**
	 * Performs a {@code try-with-resources} with Java's lame {@link AutoCloseable} interface whose {@link AutoCloseable#close()} method declares a checked
	 * exception.
	 */
	public static <C extends AutoCloseable, R, E extends Exception> R uncheckedTryGetWithResources( ThrowingFunction0<C,E> closeableFactory, //
		ThrowingFunction1<R,C,E> tryFunction )
	{
		@SuppressWarnings( "unchecked" ) ThrowingFunction0<C,RuntimeException> f = (ThrowingFunction0<C,RuntimeException>)closeableFactory;
		C closeable = f.invoke();
		assert closeable != null;
		if( areAssertionsEnabled() )
		{
			R result = unchecked( () -> tryFunction.invoke( closeable ) );
			unchecked( closeable::close );
			return result;
		}
		try
		{
			return unchecked( () -> tryFunction.invoke( closeable ) );
		}
		finally
		{
			unchecked( closeable::close );
		}
	}

	/**
	 * Performs a {@code try-with-resources} with Java's lame {@link AutoCloseable} interface whose {@link AutoCloseable#close()} method declares a checked
	 * exception.
	 */
	public static <C extends AutoCloseable, E extends Exception> void uncheckedTryWithResources( ThrowingFunction0<? super C,E> closeableFactory, //
		ThrowingProcedure1<? super C,E> tryProcedure )
	{
		@SuppressWarnings( "unchecked" ) ThrowingFunction0<C,RuntimeException> f = (ThrowingFunction0<C,RuntimeException>)closeableFactory;
		C closeable = f.invoke();
		assert closeable != null;
		if( areAssertionsEnabled() )
		{
			unchecked( () -> tryProcedure.invoke( closeable ) );
			unchecked( closeable::close );
		}
		else
		{
			try
			{
				unchecked( () -> tryProcedure.invoke( closeable ) );
			}
			finally
			{
				unchecked( closeable::close );
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Reflection

	public static final class reflect
	{
		/**
		 * Checks whether the given {@link AnnotatedElement} (class, interface, method, field, etc.) has an annotation of a given type name.
		 *
		 * @param annotatedElement the {@link AnnotatedElement} to check
		 * @param annotationName   the fully qualified name of the type of the annotation to check for.
		 *
		 * @return {@code true} of the {@link AnnotatedElement} has the given annotation; {@code false} otherwise.
		 */
		public static boolean hasAnnotation( AnnotatedElement annotatedElement, String annotationName )
		{
			for( Annotation annotation : annotatedElement.getAnnotations() )
				if( annotation.annotationType().getName().equals( annotationName ) )
					return true;
			return false;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Math

	public static final class math
	{
		/**
		 * A certain famous constant conveniently packaged in a single Greek letter as it should be.
		 */
		public static final double π = Math.PI;

		/**
		 * Checks whether two {@code double} values are equal within a given tolerance.
		 *
		 * @param a         a {@code double} value
		 * @param b         another {@code double} value
		 * @param tolerance the tolerance.
		 *
		 * @return {@code true} if the two values are equal within the given tolerance; {@code false} otherwise.
		 */
		public static boolean eq( double a, double b, double tolerance )
		{
			assert tolerance > 0;
			return Math.abs( a - b ) < tolerance;
		}

		/**
		 * Planar Interpolation.
		 */
		public static double interpolate( double x, double x1, double y1, double x2, double y2 )
		{
			// if( x2 == x1 )
			//     return (y1 + y2) / 2;
			// return y1 + (y2 - y1) * (x - x1) / (x2 - x1);

			// double t = x2 == x1? 0.5 : (x - x1)/(x2 - x1);
			// return interpolate( y1, y2, t );

			if( x2 == x1 )
				return (y1 + y2) / 2;
			double slope = (y2 - y1) / (x2 - x1);
			return y1 + slope * (x - x1);
		}

		/**
		 * Naive Linear interpolation.
		 * (See std::lerp of C++20 for how much more complicated this could be)
		 */
		public static double interpolate( double a, double b, double t )
		{
			return a + t * (b - a);
		}

		/**
		 * Cubic interpolation.
		 * from https://www.paulinternet.nl/?page=bicubic
		 */
		public static double interpolate( double[] p, double x )
		{
			return p[1] + 0.5 * x * (p[2] - p[0] + x * (2.0 * p[0] - 5.0 * p[1] + 4.0 * p[2] - p[3] + x * (3.0 * (p[1] - p[2]) + p[3] - p[0])));
		}

		/**
		 * Returns a value clamped between a given minimum and maximum.
		 *
		 * @param value the value to clamp
		 * @param min   the minimum value
		 * @param max   the maximum value
		 *
		 * @return a value between the given minimum and maximum.
		 */
		public static double clamp( double value, double min, double max )
		{
			if( value < min )
				return min;
			if( value > max )
				return max;
			return value;
		}

		/**
		 * Computes the remainder of a number according to IEEE 754.
		 * Essentially, this is supposed to be the same as the fmod() function of C++.
		 */
		public static double mod( double n, double d )
		{
			return n - d * Math.floor( n / d );
		}

		// IEEE 754 remainder operation alternative - practically same speed
		public static double mod_b( double n, double d )
		{
			return n % d - d * (Math.signum( n ) - 1) / 2.0;
		}

		// Bhaskara I's sine approximation formula
		// see https://en.wikipedia.org/wiki/Bhaskara_I%27s_sine_approximation_formula
		// this is SLOWER than Math.sin() by a factor of almost 2
		public static double sin_bhaskara1( double x )
		{
			double xx = x % π;
			double quadSine = (16 * xx * (π - xx)) / (5 * π * π - 4 * xx * (π - xx));
			return quadSine * Math.signum( π - x % (2 * π) );
		}

		/**
		 * Computes an approximation of sin(x).
		 * Milian's version
		 * see https://stackoverflow.com/a/28050328/773113
		 * this is FASTER than Math.sin() by a factor of more than 2
		 * this is accurate to about 1%
		 */
		public static double sin/*_milian*/( double x )
		{
			x = π / 2 - x; //convert to cosine
			x *= 1.0 / (2.0 * π);
			x -= 0.25 + Math.floor( x + 0.25 );
			x *= 16.0 * (Math.abs( x ) - 0.5);
			x += 0.225 * x * (Math.abs( x ) - 1.0); // for extra precision
			return x;
		}

		/**
		 * Computes an approximation of cos(x).
		 * Milian's version
		 * see https://stackoverflow.com/a/28050328/773113
		 * this is FASTER than Math.cos() by a factor of more than 2
		 * this is accurate to about 1%
		 */
		public static double cos/*_milian*/( double x )
		{
			x *= 1.0 / (2.0 * π);
			x -= 0.25 + Math.floor( x + 0.25 );
			x *= 16.0 * (Math.abs( x ) - 0.5);
			x += 0.225 * x * (Math.abs( x ) - 1.0); // for extra precision
			return x;
		}

		/**
		 * Computes an approximation of sin( x * 2π )
		 * in other words:
		 * sin( x ) = sin2pi( x / 2π )
		 * sin2pi( x ) = sin( x * 2π )
		 */
		public static double sin2pi/*_milian*/( double x )
		{
			x = 0.25 - x; //convert to cosine
			x -= 0.25 + Math.floor( x + 0.25 );
			x *= 16.0 * (Math.abs( x ) - 0.5);
			x += 0.225 * x * (Math.abs( x ) - 1.0); // for extra precision
			return x;
		}

		/**
		 * Computes an approximation of cos( x * 2π )
		 * in other words:
		 * cos( x ) = cos2pi( x / 2π )
		 * cos2pi( x ) = cos( x * 2π )
		 */
		public static double cos2pi/*_milian*/( double x )
		{
			x -= 0.25 + Math.floor( x + 0.25 );
			x *= 16.0 * (Math.abs( x ) - 0.5);
			x += 0.225 * x * (Math.abs( x ) - 1.0); // for extra precision
			return x;
		}

		// Andy's version
		// see https://stackoverflow.com/a/19827811/773113
		// This is SLOWER than Math.sin() by a factor of about 2, and a lot less precise than Milian's version
		public static double sin_andy( double t )
		{
			t = t % (π * 2);
			if( t < 0.0 )
				t = t + (π * 2);

			double x;
			if( t < π )
			{
				if( t < π / 2 )
					x = t;
				else
					x = π - t;
			}
			else
			{
				if( t < π * 1.5 )
					x = t - π;
				else
					x = (π * 2) - t;
			}

			//double result = (1.043406062 * x) / (0.2508691922 * x * x + 1.0);
			double result = (1.01815748f * x) / (0.23133484f * x * x + 1.0);
			if( t > π )
				result = -result;
			return result;
		}

		// Nick's version
		// see https://stackoverflow.com/a/66868438/773113
		// this is FASTER than Math.sin() by a factor of about 2
		public static double sin_nick( double x )
		{
			double y = mod( x, π );
			y = (4.0 / π) * y + (-4 / (π * π)) * y * y;
			// for extra precision:
			// Q: 0.775
			// P: 0.225
			y = 0.225 * (y * Math.abs( y ) - y) + y; // Q * y + P * y * abs(y)
			return y * Math.signum( π - mod( x, (2 * π) ) );
		}

		/**
		 * Computes x % 1 but much faster
		 */
		public static double mod1( double x )
		{
			assert ((double)Long.MIN_VALUE) < x && x < ((double)Long.MAX_VALUE);
			return x - (double)(long)x;
		}

		/**
		 * Computes x - (x % 1) but much faster
		 */
		public static double truncate( double x )
		{
			assert ((double)Long.MIN_VALUE) < x && x < ((double)Long.MAX_VALUE);
			return (long)x;
		}

		/**
		 * Computes x squared.
		 */
		public static double squared( double x )
		{
			return x * x;
		}

		/**
		 * Computes x cubed.
		 */
		public static double cubed( double x )
		{
			return x * x * x;
		}
	}

	//TODO: also try Pade approximation for sine
	//      sin(x) = ((12671/4363920)x^5−(2363/18183)x^3+x)/(1+(445/12122)x^2+(601/872784)x^4+(121/16662240)x^6)
	//      see https://en.wikipedia.org/wiki/Pad%C3%A9_approximant

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Arrays

	public static final class array
	{
		/**
		 * Checks whether two arrays are deeply equal.
		 *
		 * @param a   one array.
		 * @param b   another array.
		 * @param <T> the type of the array elements.
		 *
		 * @return {@link true} if the arrays are equal, that is, {@link Objects#equals} returns {@link true} for every pair of elements.
		 */
		public static <T> boolean equals( T[] a, T[] b )
		{
			if( a == b )
				return true;
			if( a == null || b == null )
				return false;
			if( a.length != b.length )
				return false;
			for( int i = 0; i < a.length; i++ )
				if( !Objects.equals( a[i], b[i] ) )
					return false;
			return true;
		}

		/**
		 * Finds the first index of an element in an array, returns -1 if not found.
		 */
		public static <T> int indexOf( T[] array, T element )
		{
			return indexOf( array, element, Objects::equals );
		}

		/**
		 * Finds the index of an element in an array, using a given {@link EqualityComparator}.
		 */
		public static <T> int indexOf( T[] array, T element, EqualityComparator<T> comparator )
		{
			for( int i = 0; i < array.length; i++ )
				if( comparator.equals( array[i], element ) )
					return i;
			return -1;
		}

		/**
		 * Creates a new array, from a given array plus an element appended at the end.  The element must not already exist.
		 */
		public static <T> T[] add( T[] array, T element )
		{
			return add( array, element, Objects::equals );
		}

		/**
		 * Creates a new array, from a given array plus an element appended at the end, using a given {@link EqualityComparator}. The element must not already
		 * exist.
		 */
		public static <T> T[] add( T[] array, T element, EqualityComparator<T> comparator )
		{
			return insertAt( array, array.length, element, comparator );
		}

		/**
		 * Creates a new array, from a given array plus an element inserted at a given index. The element must not already exist.
		 */
		public static <T> T[] insertAt( T[] array, int index, T element )
		{
			return insertAt( array, index, element, Objects::equals );
		}

		/**
		 * Creates a new array, from a given array plus an element inserted at a given index, using a given {@link EqualityComparator}. The element must not
		 * already exist.
		 */
		public static <T> T[] insertAt( T[] array, int index, T element, EqualityComparator<T> comparator )
		{
			assert !contains( array, element, comparator );
			T[] newArray = newArrayLike( array, array.length + 1 );
			if( index > 0 )
				System.arraycopy( array, 0, newArray, 0, index );
			newArray[index] = element;
			if( index < array.length )
				System.arraycopy( array, index, newArray, index + 1, array.length - index );
			return newArray;
		}

		/**
		 * Creates a new array, from a given array minus a given element. The element must exist.
		 */
		public static <T> T[] remove( T[] array, T element )
		{
			return remove( array, element, Objects::equals );
		}

		/**
		 * Creates a new array, from a given array minus a given element, using a given {@link EqualityComparator}. The element must exist.
		 */
		public static <T> T[] remove( T[] array, T element, EqualityComparator<T> comparator )
		{
			int index = indexOf( array, element, comparator );
			assert index != -1;
			return removeAt( array, index );
		}

		/**
		 * Creates a new array, from a given array minus the element at a given index.
		 */
		public static <T> T[] removeAt( T[] array, int index )
		{
			assert 0 <= index && index < array.length;
			T[] newArray = newArrayLike( array, array.length - 1 );
			if( index > 0 )
				System.arraycopy( array, 0, newArray, 0, index );
			if( index < newArray.length )
				System.arraycopy( array, index + 1, newArray, index, newArray.length - index );
			return newArray;
		}

		/**
		 * Checks whether an array contains a given element.
		 */
		public static <T> boolean contains( T[] array, T element )
		{
			return contains( array, element, Objects::equals );
		}

		/**
		 * Checks whether an array contains a given element, using a given {@link EqualityComparator}.
		 */
		public static <T> boolean contains( T[] array, T element, EqualityComparator<T> comparator )
		{
			return indexOf( array, element, comparator ) != -1;
		}

		public static <T> T[] clone( T[] array )
		{
			return Arrays.copyOf( array, array.length );
		}

		private static <T> T[] newArrayLike( T[] array, int length )
		{
			@SuppressWarnings( "unchecked" ) T[] result = (T[])Array.newInstance( array.getClass().getComponentType(), length );
			return result;
		}

		public static double[] clone( double[] array )
		{
			return Arrays.copyOf( array, array.length );
		}

		public static <T> boolean trueForAll( T[] array, BooleanFunction1<T> predicate )
		{
			for( T element : array )
				if( !predicate.invoke( element ) )
					return false;
			return true;
		}

		public static <T> boolean trueForAny( T[] array, BooleanFunction1<T> predicate )
		{
			for( T element : array )
				if( predicate.invoke( element ) )
					return true;
			return false;
		}

		public static boolean trueForAll( double[] array, BooleanFunction1Double predicate )
		{
			for( double element : array )
				if( !predicate.invoke( element ) )
					return false;
			return true;
		}

		public static boolean trueForAny( double[] array, BooleanFunction1Double predicate )
		{
			for( double element : array )
				if( predicate.invoke( element ) )
					return true;
			return false;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Testing

	public static final class testing
	{
		/**
		 * Runs a full garbage collection.
		 * <p>
		 * Adapted from <a href="https://stackoverflow.com/q/1481178/773113">Stack Overflow: Forcing Garbage Collection in Java?</a>
		 */
		public static void runGarbageCollection()
		{
			runGarbageCollection0();
			runGarbageCollection0();
		}

		private static void runGarbageCollection0()
		{
			for( WeakReference<Object> ref = new WeakReference<>( new Object() ); ; )
			{
				System.gc();
				Runtime.getRuntime().runFinalization();
				if( ref.get() == null )
					break;
				Thread.yield();
			}
		}

		public static <T extends Throwable> T expectException( Class<T> expectedThrowableClass, Procedure0 procedure )
		{
			assert expectedThrowableClass != null;
			assert procedure != null;
			Throwable throwable = invokeAndCatch( procedure );
			assert throwable != null : new ExceptionExpectedException( expectedThrowableClass );
			throwable = unwrap( throwable );
			assert throwable.getClass() == expectedThrowableClass : new ExceptionDiffersFromExpectedException( expectedThrowableClass, throwable );
			return expectedThrowableClass.cast( throwable );
		}

		private static Throwable unwrap( Throwable throwable )
		{
			for( ; ; )
			{
				if( throwable instanceof AssertionError && throwable.getCause() != null )
				{
					throwable = throwable.getCause();
					continue;
				}
				//				if( throwable instanceof UndeclaredThrowableException )
				//				{
				//					assert throwable.getCause() != null;
				//					assert throwable.getCause() == ((UndeclaredThrowableException)throwable).getUndeclaredThrowable();
				//					throwable = throwable.getCause();
				//					continue;
				//				}
				//				if( throwable instanceof InvocationTargetException )
				//				{
				//					assert throwable.getCause() != null;
				//					assert throwable.getCause() == ((InvocationTargetException)throwable).getTargetException();
				//					throwable = throwable.getCause();
				//					continue;
				//				}
				break;
			}
			return throwable;
		}

		private static Throwable invokeAndCatch( Procedure0 procedure )
		{
			try
			{
				procedure.invoke();
				return null;
			}
			catch( Throwable throwable )
			{
				return throwable;
			}
		}

		public static PrintStream nullPrintStream()
		{
			return new PrintStream( OutputStream.nullOutputStream() );
		}

		public static String withCapturedOutputStream( Procedure0 procedure )
		{
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			PrintStream printStream = new PrintStream( byteArrayOutputStream );
			PrintStream oldSystemOut = System.out;
			System.setOut( printStream );
			tryFinally( procedure, () -> System.setOut( oldSystemOut ) );
			return byteArrayOutputStream.toString( StandardCharsets.UTF_8 );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Debugging

	public static final class debugging
	{
		public static void breakPoint()
		{
			try
			{
				throw new BreakpointException();
			}
			catch( BreakpointException ignored )
			{
			}
		}
	}

	/**
	 * For information about this method, google "Sneaky throw".
	 * <p>
	 * Note: even though the method throws an exception, it is declared to also return an exception.
	 * This allows the caller to use one more `throw` statement, which, although unreachable,
	 * lets the compiler know that that execution will never proceed past that point.
	 */
	@SuppressWarnings( "unchecked" ) public static <T extends Throwable> RuntimeException sneakyException( Throwable t ) throws T
	{
		throw (T)t;
	}

	////////////////////////////

	public static final class classLoading
	{
		public static Path getPathFromClassLoaderAndTypeName( ClassLoader classLoader, String typeName )
		{
			URL url = classLoader.getResource( typeName.replace( '.', '/' ) + ".class" );
			assert url != null;
			return getPathFromUrl( url );
		}

		public static Path getPathFromUrl( URL url )
		{
			URI uri = unchecked( url::toURI );
			String scheme = uri.getScheme();
			if( scheme.equals( "jar" ) )
			{
				FileSystem fileSystem = getOrCreateFileSystem( uri );
				String jarPath = uri.getSchemeSpecificPart();
				int i = jarPath.indexOf( '!' );
				assert i >= 0;
				String subPath = jarPath.substring( i + 1 );
				return fileSystem.getPath( subPath );
			}
			if( scheme.equals( "jrt" ) )
			{
				FileSystem fileSystem = FileSystems.getFileSystem( URI.create( "jrt:/" ) );
				return fileSystem.getPath( "modules", uri.getPath() );
			}
			return Paths.get( uri );
		}

		private static FileSystem getOrCreateFileSystem( URI uri )
		{
			Optional<FileSystem> fileSystem = tryGetFileSystem( uri );
			return fileSystem.orElseGet( () -> newFileSystem( uri ) );
		}

		private static FileSystem newFileSystem( URI uri )
		{
			return unchecked( () -> FileSystems.newFileSystem( uri, Collections.emptyMap() ) );
		}

		private static Optional<FileSystem> tryGetFileSystem( URI uri )
		{
			try
			{
				/**
				 * NOTE: if you see this failing with a {@link FileSystemNotFoundException}
				 * it is probably because you have an exception breakpoint;
				 * just let it run, it will probably recover.
				 */
				FileSystem fileSystem = FileSystems.getFileSystem( uri );
				return Optional.of( fileSystem );
			}
			catch( FileSystemNotFoundException ignore )
			{
				return Optional.empty();
			}
		}

		public static Collection<String> troubleshoot( ClassLoader classLoader )
		{
			ArrayList<String> mutableContents = new ArrayList<>();
			addContents( mutableContents, classLoader );
			return mutableContents;
		}

		private static void addContents( ArrayList<String> mutableContents, ClassLoader classLoader )
		{
			ClassLoader parentClassLoader = classLoader.getParent();
			if( parentClassLoader != null )
				addContents( mutableContents, parentClassLoader );

			if( classLoader instanceof URLClassLoader urlClassLoader )
			{
				List<URL> urls = Arrays.stream( urlClassLoader.getURLs() ).toList();
				mutableContents.addAll( urls.stream().map( u -> classLoaderIdentityString( urlClassLoader ) + " " + u.toString() ).toList() );
				return;
			}

			mutableContents.add( "Unknown classloader: " + classLoaderIdentityString( classLoader ) );
			List<URL> urls = iterator.toList( unchecked( () -> classLoader.getResources( "." ) ).asIterator() );
			mutableContents.addAll( urls.stream().map( u -> classLoaderIdentityString( classLoader ) + " " + u.toString() ).toList() );
		}

		private static String classLoaderIdentityString( ClassLoader classLoader )
		{
			return "'" + classLoader.getName() + "' " + " " + identityString( classLoader );
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Tree

	public static final class tree
	{
		public static <T> void print( T rootNode, Function1<Iterable<T>,T> breeder, Function1<String,T> stringizer, Procedure1<String> emitter )
		{
			StringBuilder stringBuilder = new StringBuilder();
			printTreeRecursive( stringBuilder, "", rootNode, "", breeder, stringizer, emitter );
		}

		private static final String midLeaf = "├─";
		private static final String endLeaf = "└─";
		private static final String midNode = "│ ";
		private static final String endNode = "  ";
		private static final String terminal = "■ ";

		private static <T> void printTreeRecursive( StringBuilder stringBuilder, String parentPrefix, T node, String childPrefix, Function1<Iterable<T>,T> breeder, Function1<String,T> stringizer, Procedure1<String> emitter )
		{
			Iterator<T> iterator = breeder.invoke( node ).iterator();
			int position = stringBuilder.length();
			stringBuilder.append( parentPrefix ).append( terminal );
			stringBuilder.append( stringizer.invoke( node ) );
			emitter.invoke( stringBuilder.toString() );
			stringBuilder.setLength( position );
			stringBuilder.append( childPrefix );
			while( iterator.hasNext() )
			{
				T childNode = iterator.next();
				boolean mid = iterator.hasNext();
				printTreeRecursive( stringBuilder, mid ? midLeaf : endLeaf, childNode, mid ? midNode : endNode, breeder, stringizer, emitter );
			}
			stringBuilder.setLength( position );
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Synchronization

	public static void synchronize( Lock lock, Procedure0 procedure )
	{
		if( areAssertionsEnabled() )
		{
			lock.lock();
			procedure.invoke();
			lock.unlock();
		}
		else
		{
			lock.lock();
			try
			{
				procedure.invoke();
			}
			finally
			{
				lock.unlock();
			}
		}
	}

	public static <T> T synchronize( Lock lock, Function0<T> function )
	{
		if( areAssertionsEnabled() )
		{
			lock.lock();
			T result = function.invoke();
			lock.unlock();
			return result;
		}
		else
		{
			lock.lock();
			try
			{
				return function.invoke();
			}
			finally
			{
				lock.unlock();
			}
		}
	}

	public static <R> R invoke( Function0<R> function )
	{
		return function.invoke();
	}

	public static <T> boolean isAssignable( Class<T> class1, Class<? extends T> class2 )
	{
		assert !class1.isPrimitive();
		assert !class2.isPrimitive();
		return class1.isAssignableFrom( class2 );
	}

	public static <S> Optional<Class<S>> as( Class<S> jvmClass1, Class<?> jvmClass2 )
	{
		if( !jvmClass1.isAssignableFrom( jvmClass2 ) )
			return Optional.empty();
		@SuppressWarnings( "unchecked" ) Class<S> result = (Class<S>)jvmClass2;
		return Optional.of( result );
	}

	/**
	 * Checks whether two objects are equal by value.
	 * <p>
	 * Unlike the {@link Objects#equals(Object, Object)} method, this method uses generics to make sure at compile time that the objects being compared are indeed of compatible
	 * types.  Also, at runtime, it asserts the same thing.
	 * <p>
	 * An example of a case where this is useful is when comparing a java.util.Date and a {@link java.sql.Timestamp}: Objects.equals( utilDate, sqlTimestamp ) will work,
	 * but Objects.equals( sqlTimestamp, utilDate ) will silently yield false negatives. (A nasty bug to track down.)
	 * <p>
	 * With this method, equalByValue( utilDate, sqlTimestamp ) will work, equalByValue( sqlTimestamp, utilDate ) will not compile, and equalByValue( (java.util.Date)sqlTimestamp,
	 * utilDate ) will result in an assertion failure instead of silently yielding false negatives.
	 *
	 * @param a   an object.
	 * @param b   another object of same or derived class.
	 * @param <T> the type of the first object.
	 * @param <U> the type of the second object.
	 *
	 * @return {@code true} if the objects are equal by value.
	 */
	public static <T, U extends T> boolean equalByValue( T a, U b )
	{
		assert a == null || b == null || isAssignable( getClass( a ), getClass( b ) ) : getClass( a ) + " [" + a + "] " + getClass( b ) + " [" + b + "]";
		return Objects.equals( a, b );
	}

	/**
	 * Checks whether two objects are equal by reference.
	 * <p>
	 * Useful to avoid, for example, the "Array objects are compared using '==', not 'Arrays.equals()'" warning when trying to compare arrays by reference.
	 */
	public static <T, U extends T> boolean equalByReference( T a, U b )
	{
		assert a == null || b == null || isAssignable( getClass( a ), getClass( b ) ) : getClass( a ) + " [" + a + "] " + getClass( b ) + " [" + b + "]";
		return a == b;
	}

	private static class PrimitiveInfo<T>
	{
		final Class<T> primitiveClass;
		final Class<T> wrapperClass;
		private PrimitiveInfo( Class<T> primitiveClass, Class<T> wrapperClass )
		{
			this.primitiveClass = primitiveClass;
			this.wrapperClass = wrapperClass;
		}
	}

	private static final List<PrimitiveInfo<?>> primitiveTypeInfo = List.of( new PrimitiveInfo<>( boolean.class /**/, Boolean.class   /**/ ), new PrimitiveInfo<>( char.class    /**/, Character.class /**/ ), new PrimitiveInfo<>( byte.class    /**/, Byte.class      /**/ ), new PrimitiveInfo<>( short.class   /**/, Short.class     /**/ ), new PrimitiveInfo<>( int.class     /**/, Integer.class   /**/ ), new PrimitiveInfo<>( long.class    /**/, Long.class      /**/ ), new PrimitiveInfo<>( float.class   /**/, Float.class     /**/ ), new PrimitiveInfo<>( double.class  /**/, Double.class    /**/ ), new PrimitiveInfo<>( void.class    /**/, Void.class      /**/ ) );

	private static int indexOfPrimitiveType( Class<?> clazz )
	{
		int n = primitiveTypeInfo.size();
		for( int i = 0; i < n; i++ )
			if( clazz == primitiveTypeInfo.get( i ).primitiveClass )
				return i;
		return -1;
	}

	private static int indexOfPrimitiveWrapperType( Class<?> clazz )
	{
		int n = primitiveTypeInfo.size();
		for( int i = 0; i < n; i++ )
			if( clazz == primitiveTypeInfo.get( i ).wrapperClass )
				return i;
		return -1;
	}

	/**
	 * Checks whether a class is a primitive wrapper type.
	 *
	 * @param clazz the class to check.
	 *
	 * @return {@code true} if the class is a primitive wrapper type. (One of Boolean, Character, Byte, Short, Integer, Long, Float, Double and Void.)
	 */
	public static boolean isPrimitiveWrapperType( Class<?> clazz )
	{
		return indexOfPrimitiveWrapperType( clazz ) != -1;
	}

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

	/**
	 * Gets the wrapper type of the given primitive type.
	 *
	 * @param clazz the primitive class whose wrapper is requested.
	 *
	 * @return the class of the wrapper type for the given type, or null if the given type was not a primitive type.
	 */
	public static <T> Class<T> getPrimitiveWrapperType( Class<T> clazz )
	{
		assert clazz.isPrimitive();
		int i = indexOfPrimitiveType( clazz );
		assert i != -1;
		return uncheckedClassCast( primitiveTypeInfo.get( i ).wrapperClass );
	}
}
