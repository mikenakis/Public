package io.github.mikenakis.kit;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.github.mikenakis.debug.Debug;
import io.github.mikenakis.kit.collections.FilteringIterable;
import io.github.mikenakis.kit.collections.MappingIterable;
import io.github.mikenakis.kit.collections.OptionalsFlatMappingIterable;
import io.github.mikenakis.kit.collections.UnmodifiableIterable;
import io.github.mikenakis.kit.collections.UnmodifiableIterator;
import io.github.mikenakis.kit.exceptions.LengthMustBeNonNegativeException;
import io.github.mikenakis.kit.exceptions.OffsetMustBeNonNegativeException;
import io.github.mikenakis.kit.exceptions.OffsetMustNotExceedSizeException;
import io.github.mikenakis.kit.exceptions.OffsetPlusCountMustNotExceedSizeException;
import io.github.mikenakis.kit.exceptions.UncheckedException;
import io.github.mikenakis.kit.functional.BooleanFunction0;
import io.github.mikenakis.kit.functional.BooleanFunction1;
import io.github.mikenakis.kit.functional.Function0;
import io.github.mikenakis.kit.functional.Function1;
import io.github.mikenakis.kit.functional.Procedure0;
import io.github.mikenakis.kit.functional.Procedure1;
import io.github.mikenakis.kit.functional.ThrowingFunction0;
import io.github.mikenakis.kit.functional.ThrowingProcedure0;
import io.github.mikenakis.kit.logging.Log;
import io.github.mikenakis.kit.ref.Ref;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.management.ManagementFactory;
import java.lang.ref.WeakReference;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteOrder;
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
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@SuppressWarnings( "NewClassNamingConvention" )
public final class Kit
{
	private static final Duration postAndWaitTimeout = Duration.ofSeconds( 2 );

	public static final byte[] ARRAY_OF_ZERO_BYTES = new byte[0];
	public static final Object[] ARRAY_OF_ZERO_OBJECTS = new Object[0];

	private Kit() { }

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
	public static <T> T fail()
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
		return object.getClass().getName() + "@" + Integer.toHexString( identityHashCode );
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Stacks, Stack Traces, Source code locations

	private static StackWalker getStackWalker()
	{
		return StackWalker.getInstance( EnumSet.noneOf( StackWalker.Option.class ) ); // StackWalker.Option.RETAIN_CLASS_REFERENCE );;
	}

	public static StackWalker.StackFrame getStackFrame( int numberOfFramesToSkip )
	{
		assert numberOfFramesToSkip > 0;
		return getStackWalker().walk( s -> iterator.getSingleElement( s.skip( numberOfFramesToSkip + 1 ).limit( 1 ).iterator() ) );
	}

	public static StackWalker.StackFrame[] getStackTrace( int numberOfFramesToSkip )
	{
		assert numberOfFramesToSkip > 0;
		return getStackWalker().walk( s -> s.skip( numberOfFramesToSkip + 1 ).toArray( StackWalker.StackFrame[]::new ) );
	}

	public static SourceLocation getSourceLocation( int numberOfFramesToSkip )
	{
		StackWalker.StackFrame stackFrame = getStackFrame( numberOfFramesToSkip + 1 );
		return SourceLocation.fromStackFrame( stackFrame );
	}

	public static String stringFromThrowable( Throwable throwable )
	{
		return stringFromThrowable( "", throwable );
	}

	public static String stringFromThrowable( String message, Throwable throwable )
	{
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter( stringWriter );
		if( !message.isEmpty() )
		{
			printWriter.print( message );
			printWriter.print( ": " );
		}
		throwable.printStackTrace( printWriter );
		return stringWriter.toString();
	}

	public static boolean assertWeakly( boolean value )
	{
		if( areAssertionsEnabled() && !value )
		{
			Log.message( Log.Level.ERROR, 1, "Assertion failure" );
			Debug.breakPoint();
			return false;
		}
		return true;
	}

	public static <T> Optional<T> filterOptional( T value, BooleanFunction1<T> predicate )
	{
		return predicate.invoke( value ) ? Optional.of( value ) : Optional.empty();
	}

	/**
	 * Runs a full garbage collection.
	 * <p>
	 * From <a href="https://stackoverflow.com/a/72694056/773113">Stack Overflow: How to force garbage collection in Java?</a>
	 */
	public static void runGarbageCollection()
	{
		long freeMemory = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
		for( ; ; )
		{
			Runtime.getRuntime().gc();
			unchecked( () -> Thread.sleep( 100 ) );
			long newFreeMemory = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
			if( newFreeMemory == freeMemory )
				break;
			freeMemory = newFreeMemory;
		}
	}

	private static final AtomicReference<Object> oldObjectRef = new AtomicReference<>( allocateObject() );

	private static WeakReference<Object> allocateWeakReference()
	{
		Object newObject = allocateObject();
		Object oldObject = oldObjectRef.getAndSet( newObject );
		return new WeakReference<>( oldObject );
	}

	private static Object allocateObject()
	{
		return new Object();
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
			assert length >= 0 : new LengthMustBeNonNegativeException( length );
			assert aOffset >= 0 : new OffsetMustBeNonNegativeException( aOffset );
			assert aOffset <= aBytes.length : new OffsetMustNotExceedSizeException( aOffset, aBytes.length );
			assert aOffset + length <= aBytes.length : new OffsetPlusCountMustNotExceedSizeException( aOffset, length, aBytes.length );
			return Arrays.compare( aBytes, aOffset, aOffset + length, bBytes, bOffset, bOffset + length );
//			for( int i = 0; i < length; i++ )
//			{
//				int difference = aBytes[aOffset + i] - bBytes[bOffset + i];
//				if( difference != 0 )
//					return difference;
//			}
//			return 0;
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
			assert length >= 0 : new LengthMustBeNonNegativeException( length );
			assert offset >= 0 : new OffsetMustBeNonNegativeException( offset );
			assert offset <= data.length : new OffsetMustNotExceedSizeException( offset, data.length );
			assert offset + length <= data.length : new OffsetPlusCountMustNotExceedSizeException( offset, length, data.length );
			int end = offset + length;
			for( int i = offset; i < end; i++ )
				if( data[i] == byteToFind )
					return i;
			return -1;
		}

		public static int lastIndexOf( byte[] data, int offset, int length, byte byteToFind )
		{
			assert length >= 0 : new LengthMustBeNonNegativeException( length );
			assert offset >= 0 : new OffsetMustBeNonNegativeException( offset );
			assert offset <= data.length : new OffsetMustNotExceedSizeException( offset, data.length );
			assert offset + length <= data.length : new OffsetPlusCountMustNotExceedSizeException( offset, length, data.length );
			int end = offset + length;
			for( int i = end - 1; i >= offset; i-- )
				if( data[i] == byteToFind )
					return i;
			return -1;
		}

		public static int indexOfAnyOf( byte[] data, int offset, int length, byte[] bytes )
		{
			assert length >= 0 : new LengthMustBeNonNegativeException( length );
			assert offset >= 0 : new OffsetMustBeNonNegativeException( offset );
			assert offset <= data.length : new OffsetMustNotExceedSizeException( offset, data.length );
			assert offset + length <= data.length : new OffsetPlusCountMustNotExceedSizeException( offset, length, data.length );
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
			assert length >= 0 : new LengthMustBeNonNegativeException( length );
			assert offset >= 0 : new OffsetMustBeNonNegativeException( offset );
			assert offset <= data.length : new OffsetMustNotExceedSizeException( offset, data.length );
			assert offset + length <= data.length : new OffsetPlusCountMustNotExceedSizeException( offset, length, data.length );
			if( length < pattern.length )
				return -1;
			if( pattern.length == 0 )
				return 0;
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
			assert length >= 0 : new LengthMustBeNonNegativeException( length );
			assert offset >= 0 : new OffsetMustBeNonNegativeException( offset );
			assert offset <= data.length : new OffsetMustNotExceedSizeException( offset, data.length );
			assert offset + length <= data.length : new OffsetPlusCountMustNotExceedSizeException( offset, length, data.length );
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
			assert length >= 0 : new LengthMustBeNonNegativeException( length );
			assert offset >= 0 : new OffsetMustBeNonNegativeException( offset );
			assert offset <= data.length : new OffsetMustNotExceedSizeException( offset, data.length );
			assert offset + length <= data.length : new OffsetPlusCountMustNotExceedSizeException( offset, length, data.length );
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
			return hexString( bytes, "" );
		}

		public static String hexString( byte[] bytes, String delimiter )
		{
			StringBuilder stringBuilder = new StringBuilder();
			boolean first = true;
			for( byte b : bytes )
			{
				if( first )
					first = false;
				else
					stringBuilder.append( delimiter );
				stringBuilder.append( hex( b ) );
			}
			return stringBuilder.toString();
		}

		public static void toHex( byte value, char[] chars, int offset )
		{
			int v = value & 0xFF;
			chars[offset] = HEX_DIGITS[v >>> 4];
			chars[offset + 1] = HEX_DIGITS[v & 0x0F];
		}

		public static String hex( byte value )
		{
			char[] chars = new char[2];
			toHex( value, chars, 0 );
			return new String( chars );
		}

		public static int nybble( char c )
		{
			if( c >= '0' && c <= '9' )
				return c - '0';
			if( c >= 'a' && c <= 'f' )
				return 10 + c - 'a';
			if( c >= 'A' && c <= 'F' )
				return 10 + c - 'A';
			assert false;
			return 0;
		}

		public static byte fromHex( String s, int offset )
		{
			int hi = nybble( s.charAt( offset ) );
			int lo = nybble( s.charAt( offset + 1 ) );
			return (byte)((hi << 4) | lo);
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
		 * Appends the string representation of an {@link Object} to a {@link StringBuilder}. The difference between this function and
		 * {@link StringBuilder#append(Object)} is that this function treats {@link String} differently: strings are output escaped and surrounded with quotes.
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
		 * <p>
		 * Corrects Java's insanity of only offering versions of this function that work with regular expressions.
		 *
		 * @param stringToSplit the string to split.
		 * @param delimiter     the delimiter.
		 *
		 * @return An array of {@link String}. If the delimiter is not found, the array will contain a single element, which will be the entire source string.
		 */
		public static String[] split( String stringToSplit, char delimiter )
		{
			return split( stringToSplit, delimiter, Integer.MAX_VALUE );
		}

		/**
		 * Splits a string in parts using a given character as delimiter.
		 * <p>
		 * Corrects Java's insanity of only offering versions of this function that work with regular expressions.
		 *
		 * @param stringToSplit the string to split.
		 * @param delimiter     the delimiter.
		 * @param maximum       the maximum number of parts to return. If this number is reached, the last returned part will be the remainder of the string.
		 *
		 * @return An array of {@link String} containing the parts of the string.
		 */
		public static String[] split( String stringToSplit, char delimiter, int maximum )
		{
			return split( stringToSplit, delimiter, maximum, false );
		}

		/**
		 * Splits a string in parts using a given character as delimiter.
		 * <p>
		 * Corrects Java's insanity of only offering versions of this function that work with regular expressions.
		 *
		 * @param stringToSplit the string to split.
		 * @param delimiter     the delimiter.
		 * @param trim          whether whitespace before and after each part should be trimmed.
		 *
		 * @return An array of {@link String} containing the parts of the string.
		 */
		public static String[] split( String stringToSplit, char delimiter, boolean trim )
		{
			return split( stringToSplit, delimiter, Integer.MAX_VALUE, trim );
		}

		/**
		 * Splits a string in parts using a given character as delimiter.
		 * <p>
		 * Corrects Java's insanity of only offering versions of this function that work with regular expressions.
		 *
		 * @param stringToSplit the string to split.
		 * @param delimiter     the delimiter.
		 * @param maximum       the maximum number of parts to return. If this number is reached, the last returned part will be the remainder of the string.
		 * @param trim          whether whitespace before and after each part should be trimmed.
		 *
		 * @return An array of {@link String} containing the parts of the string.
		 */
		public static String[] split( String stringToSplit, char delimiter, int maximum, boolean trim )
		{
			return split( stringToSplit, "" + delimiter, maximum, trim );
		}

		/**
		 * Splits a string in parts using a given string as delimiter.
		 * <p>
		 * Corrects Java's insanity of only offering versions of this function that work with regular expressions.
		 *
		 * @param stringToSplit the string to split.
		 * @param delimiter     the delimiter.
		 *
		 * @return An array of {@link String}. If the delimiter is not found, the array will contain a single element, which will be the entire source string.
		 */
		public static String[] split( String stringToSplit, String delimiter )
		{
			return split( stringToSplit, delimiter, Integer.MAX_VALUE );
		}

		/**
		 * Splits a string in parts using a given string as delimiter.
		 * <p>
		 * Corrects Java's insanity of only offering versions of this function that work with regular expressions.
		 *
		 * @param stringToSplit the string to split.
		 * @param delimiter     the delimiter.
		 * @param maximum       the maximum number of parts to return. If this number is reached, the last returned part will be the remainder of the string.
		 *
		 * @return An array of {@link String} containing the parts of the string.
		 */
		public static String[] split( String stringToSplit, String delimiter, int maximum )
		{
			return split( stringToSplit, delimiter, maximum, false );
		}

		/**
		 * Splits a string in parts using a given string as delimiter.
		 * <p>
		 * Corrects Java's insanity of only offering versions of this function that work with regular expressions.
		 *
		 * @param stringToSplit the string to split.
		 * @param delimiter     the delimiter.
		 * @param trim          whether whitespace before and after each part should be trimmed.
		 *
		 * @return An array of {@link String} containing the parts of the string.
		 */
		public static String[] split( String stringToSplit, String delimiter, boolean trim )
		{
			return split( stringToSplit, delimiter, Integer.MAX_VALUE, trim );
		}

		/**
		 * Splits a string in parts using a given string as delimiter.
		 * <p>
		 * Corrects Java's insanity of only offering versions of this function that work with regular expressions.
		 *
		 * @param stringToSplit the string to split.
		 * @param delimiter     the delimiter.
		 * @param maximum       the maximum number of parts to return. If this number is reached, the last returned part will be the remainder of the string.
		 * @param trim          whether whitespace before and after each part should be trimmed.
		 *
		 * @return An array of {@link String} containing the parts of the string.
		 */
		public static String[] split( String stringToSplit, String delimiter, int maximum, boolean trim )
		{
			assert maximum >= 2;
			Collection<String> result = new ArrayList<>();
			for( int position = 0; position < stringToSplit.length(); )
			{
				int start = position;
				if( result.size() >= maximum - 1 )
					position = stringToSplit.length();
				else
				{
					position = stringToSplit.indexOf( delimiter, start );
					if( position == -1 )
						position = stringToSplit.length();
				}
				int end = position;
				if( trim )
				{
					while( start < end && Character.isWhitespace( stringToSplit.charAt( start ) ) )
						start++;
					while( start < end && Character.isWhitespace( stringToSplit.charAt( end - 1 ) ) )
						end--;
				}
				String current = stringToSplit.substring( start, end );
				result.add( current );
				position += delimiter.length();
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
		 * Unescape a Java string. Takes a string enclosed in either in single or double quotes and possibly containing Java escape sequences, and returns the
		 * content of the string with the quotes removed and the escape sequences replaced with the actual characters that they represent.
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
		 * Checks whether a string is empty or consists of only whitespace. NOTE: the only characters considered as whitespace are ' ', '\t', '\n', and '\r'.
		 */
		public static boolean isEmptyOrWhitespace( String s )
		{
			for( int i = 0; i < s.length(); i++ )
				if( !isWhitespace( s.charAt( i ) ) )
					return false;
			return true;
		}

		/**
		 * Checks whether a character is whitespace. NOTE: the only characters considered as whitespace are ' ', '\t', '\n', and '\r'.
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
		 * Gets the string representation of an {@link Object}. see {@link stringBuilder#append(StringBuilder, Object)}
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

		public static String replaceAll( String original, String subString, String replacement, boolean ignoreCase )
		{
			assert !subString.isEmpty();
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
				while( indexOf( original, subString, j + 1, ignoreCase ) == j + 1 )
					j++;
				i = j;
				builder.append( replacement );
				i += subString.length();
			}
			return builder.toString();
		}

		private static int indexOf( String str, String stringToFind, int fromIndex, boolean ignoreCase )
		{
			assert !stringToFind.isEmpty();
			int end = str.length();
			if( end == 0 )
				return fromIndex;
			end -= stringToFind.length() - 1;
			if( end < 0 )
				return -1;
			char c = stringToFind.charAt( 0 );
			for( int i = fromIndex; i < end; i++ )
			{
				i = indexOf( str, c, i, end, ignoreCase );
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

		public static Try<Integer> tryParseInteger( String text ) { return Try.of( () -> Integer.parseInt( text ) ); }
		public static Try<Long> tryParseLong( String text ) { return Try.of( () -> Long.parseLong( text ) ); }
		public static Try<Float> tryParseFloat( String text ) { return Try.of( () -> Float.parseFloat( text ) ); }
		public static Try<Double> tryParseDouble( String text ) { return Try.of( () -> Double.parseDouble( text ) ); }
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

		// PEARL: Windows has a stupid notion of a "current directory", which is a mutable global variable of process-wide scope.
		//        This means that any thread can modify it, and all other threads will be affected by the modification.
		//        (And in a DotNet process, any AppDomain can modify it, and all other AppDomains will be affected! So much for AppDomain isolation!)
		//        Java does not exactly have such a notion, but the "user.dir" system property (which you can get and set) is effectively the same.
		// NOTE:  When maven is running tests, the "user.dir" system property contains the root directory of the current module being tested.
		//        When testana is running tests, it sets the "user.dir" property accordingly.
		//        Thus, when running tests either via maven or via testana, we can obtain the path to the root directory of the current module.
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
		 * The advantage of using this algorithm over java's built-in sorting algorithm is that this one will not throw IllegalArgumentException ("Comparison
		 * method violates its general contract!") alternatively, we can issue System.setProperty( "java.util.Arrays.useLegacyMergeSort", "true" ); and then go
		 * ahead and make use of List.sort(), but modifying the state of the JVM is a lame thing to do.
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
	// Java Stream<T>

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
			return fromStream( Stream.concat( stream.fromIterable( iterable ), stream.fromIterable( other ) ) );
		}

		public static <T> Iterable<T> fromStream( Stream<T> stream )
		{
			return stream::iterator;
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

		public static <T> Iterable<T> reverse( Iterable<T> iterable )
		{
			//TODO: optimize
			return stream.fromIterable( iterable ) //
				.collect( Collectors.collectingAndThen( Collectors.toList(), l -> //
				{
					Collections.reverse( l );
					return l;
				} ) );
		}

		public static <T> Iterable<T> filter( Iterable<T> iterable, BooleanFunction1<T> filter )
		{
			return new FilteringIterable<>( iterable, filter );
		}

		public static <T, F> Iterable<T> map( Iterable<F> iterable, Function1<T,F> converter )
		{
			return new MappingIterable<>( iterable, converter );
		}

		public static <T, F> Iterable<T> flatMapOptionals( Iterable<F> iterable, Function1<Optional<T>,F> converterAndFilterer )
		{
			return new OptionalsFlatMappingIterable<>( iterable, converterAndFilterer );
		}

		public static <T> List<T> toList( Iterable<T> iterable )
		{
			List<T> result = new ArrayList<>();
			for( T element : iterable )
				result.add( element );
			return Collections.unmodifiableList( result );
			//return collection.stream.fromIterable( iterable ).collect( Collectors.toList() );
		}

		public static <T, F> Iterable<T> filterAndCast( Iterable<F> iterable, Class<T> elementClass )
		{
			Iterable<F> filtered = filter( iterable, elementClass::isInstance );
			return map( filtered, elementClass::cast );
		}

		public static <T> Iterable<T> unmodifiable( Iterable<T> delegee )
		{
			return new UnmodifiableIterable<>( delegee );
		}

		public static <K, V, T> Map<K,V> toMap( Iterable<T> iterable, Function1<K,T> keyExtractor, Function1<V,T> valueExtractor )
		{
			return stream.fromIterable( iterable ).collect( Collectors.toMap( keyExtractor::invoke, valueExtractor::invoke, Kit::dummyMergeFunction, LinkedHashMap::new ) );
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
			return stream.fromIterable( iterable ).map( Object::toString ).collect( Collectors.joining( delimiter ) );
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

		public static <T> T getSingleElement( Iterator<T> iterator )
		{
			assert iterator.hasNext(); //no elements
			T result = iterator.next();
			assert !iterator.hasNext(); //more than one element
			return result;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Java Collection<T>

	public static final class collection
	{
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
		 * Adds an item to a {@link Collection}. The item must not already exist. Corresponds to Java's {@link Collection#add(T)}, except that it corrects
		 * Java's deplorable dumbfuckery of not throwing an exception when the item already exists.
		 */
		/*@SuppressWarnings( "deprecation" )*/
		public static <T> void add( Collection<T> collection, T item )
		{
			assert item != null;
			boolean ok = collection.add( item );
			assert ok;
		}

		/**
		 * Tries to add an item to a {@link Collection}. Corresponds to Java's {@link Collection#add(T)}.
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
		 * Adds an item in a {@link Collection} or replaces it if it already exists. Corresponds to Java's {@link Collection#add(T)}.
		 */
		/*@SuppressWarnings( "deprecation" )*/
		public static <T> void addOrReplace( Collection<T> collection, T item )
		{
			assert !(collection instanceof List);
			collection.add( item );
		}

		/**
		 * Removes an item from a {@link Collection}. The item must already exist. Corresponds to Java's {@link Collection#remove(T)}, except that it corrects
		 * Java's deplorable dumbfuckery of not throwing an exception when the item does not exist.
		 */
		/*@SuppressWarnings( "deprecation" )*/
		public static <T> void remove( Collection<T> collection, T item )
		{
			boolean ok = collection.remove( item );
			assert ok;
		}

		/**
		 * Removes an item from a {@link Collection} if it exists. Corresponds to Java's {@link Collection#remove(T)}.
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
		 * Returns a new {@link ArrayList} containing the elements of the given {@link Collection} in reverse order. Important note: the returned list is a new
		 * mutable {@link ArrayList}, it is not just a reverse mapping onto the original collection.
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
					return collections.stream().mapToInt( Collection::size ).sum();
				}
			};
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Java Map<K,V>

	public static final class map
	{
		/**
		 * Gets a value by key from a {@link Map}. The key must exist. Corresponds to Java's {@link Map#get(K)}, except that it corrects Java's deplorable
		 * dumbfuckery of not throwing an exception when the key does not exist.
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
		 * Tries to get a value by key from a {@link Map}. The key may and may not exist. Corresponds to Java's {@link Map#get(K)}, the difference being that by
		 * using this method we are documenting the fact that we are intentionally allowing the key to potentially not exist and that {@code null} may be
		 * returned.
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
		 * Checks whether a {@link Map} contains a given key. Corresponds to Java's {@link Map#containsKey(K)}.
		 *
		 * @return {@code true} if the {@link Map} contains the key; {@code false} otherwise.
		 */
		/*@SuppressWarnings( "deprecation" )*/
		public static <K, V> boolean containsKey( Map<K,V> map, K key )
		{
			assert key != null;
			return map.containsKey( key );
		}

		/**
		 * Adds a key-value pair to a {@link Map}. The key must not already exist. Corresponds to Java's {@link Map#put(K, V)}, except that it corrects Java's
		 * deplorable dumbfuckery of not throwing an exception when the key already exists.
		 */
		/*@SuppressWarnings( "deprecation" )*/
		public static <K, V> void add( Map<K,V> map, K key, V value )
		{
			assert key != null;
			assert value != null;
			Object previous = map.put( key, value );
			assert previous == null : "key = {" + key + "}; value = {" + value + "}; existing value = " + previous;
		}

		/**
		 * Removes a key-value pair from a {@link Map}. The key must already exist. Corresponds to Java's {@link Map#remove(K)}, except that it corrects Java's
		 * deplorable dumbfuckery of not throwing an exception when the key does not exist.
		 */
		/*@SuppressWarnings( "deprecation" )*/
		public static <K, V> V remove( Map<K,V> map, K key )
		{
			V previous = map.remove( key );
			assert previous != null;
			return previous;
		}

		/**
		 * Removes a key-value pair from a {@link Map}. The key must already exist and must map to the given value. Corresponds to Java's {@link Map#remove(K)},
		 * except that it corrects Java's deplorable dumbfuckery of not throwing an exception when the key does not exist or when the key is not mapped to the
		 * expected value.
		 */
		/*@SuppressWarnings( "deprecation" )*/
		public static <K, V> void remove( Map<K,V> map, K key, V value )
		{
			V previous = remove( map, key );
			assert previous.equals( value ) : "key = {" + key + "}; previous = {" + previous + "}; value = {" + value + "}";
		}

		/**
		 * Tries to remove a key-value pair from a {@link Map}. Corresponds to Java's {@link Map#remove(K)}, but with a name that documents exactly what it
		 * does.
		 *
		 * @return the value that was previously associated with the given key, or {@code null} if the key was not in the map.
		 */
		/*@SuppressWarnings( "deprecation" )*/
		public static <K, V> V tryRemove( Map<? extends K,V> map, K key )
		{
			return map.remove( key );
		}

		/**
		 * Removes a key from a {@link Map}, if the key is present. The key may and may not already exist. Corresponds to Java's {@link Map#remove(K)}, the
		 * difference being that by using this method we are documenting the fact that we are intentionally allowing the key to potentially not exist.
		 */
		/*@SuppressWarnings( "deprecation" )*/
		public static <K, V> void removeIfPresent( Map<K,V> map, K key )
		{
			map.remove( key );
		}

		/**
		 * Tries to add a key-value pair to a {@link Map}. Corresponds to Java's {@link Map#put(K, V)}, the difference being that by using this method we are
		 * documenting the fact that we are intentionally allowing the key to already exist, potentially.
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
			assert previous.equals( value ) : "key = {" + key + "}; previous = {" + previous + "}; value = {" + value + "}";
			return false;
		}

		/**
		 * Tries to replace the value associated with a given key. If the key already exists, then the value associated with it is replaced and {@code true} is
		 * returned. If the key does not already exist, then the map is not modified and {@code false} is returned.
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
		 * Replaces the value associated with a given key. The key must already exist. Corresponds to Java's {@link Map#put(K, V)}, except that it corrects
		 * Java's deplorable dumbfuckery of not throwing an exception when the key does not exist.
		 */
		/*@SuppressWarnings( "deprecation" )*/
		public static <K, V> V replace( Map<K,V> map, K key, V value )
		{
			assert key != null;
			assert value != null;
			V old = map.put( key, value );
			assert old != null;
			return old;
		}

		/**
		 * Adds or replaces a key-value pair in a {@link Map}. The key may and may not already exist. Corresponds to Java's {@link Map#put}, the difference
		 * being that by using this method we are documenting the fact that we are intentionally performing this very odd and rarely used operation, allowing
		 * the key to either exist or not exist.
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
		private static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm:ss", Locale.ROOT ).withZone( ZoneId.systemDefault() );

		/**
		 * Obtains the current time coordinate in seconds, from some arbitrary origin, with as much precision as the underlying architecture allows.
		 */
		public static double timeSeconds()
		{
			return timeSeconds( false );
		}

		/**
		 * Obtains the current time coordinate optionally after waiting until the next discrete moment in time. See {@link #timeSeconds()}
		 */
		public static double timeSeconds( boolean waitForNextDiscreteMoment )
		{
			long nanos = System.nanoTime();
			if( waitForNextDiscreteMoment )
				for( long temp = nanos; nanos == temp; )
					nanos = System.nanoTime();
			return nanos * 1e-9;
		}

		/**
		 * Obtains the string representation of a given {@link Instant} (presumed to be in UTC) as a local time, in a format similar to ISO 8601 but without the
		 * ISO 8601 weirdness.
		 *
		 * @param instant the {@link Instant} to convert.
		 *
		 * @return the local time represented by the instant as an ISO-8601-ish string.
		 */
		public static String localTimeString( Instant instant )
		{
			return timeFormat.format( instant );
		}

		/**
		 * Creates an {@link Instant} at a specific year, month, day, hour, minute, second, and millisecond UTC.
		 * <p>
		 * PEARL: Java makes it surprisingly difficult to create an {@link Instant} at a specific point in time. This function does it.
		 */
		public static Instant createInstant( int year, int month, int day, int hour, int minute, int second, int millisecond )
		{
			return ZonedDateTime.of( year, month, day, hour, minute, second, millisecond * 1000 * 1000, ZoneOffset.UTC ).toInstant();
		}

		/**
		 * Creates a {@link Duration} from a given {@code double} number of seconds.
		 * <p>
		 * PEARL: Java makes it surprisingly difficult to create a {@link Duration} from a given {@code double} number of seconds. This function does it.
		 */
		public static Duration durationFromSeconds( double seconds )
		{
			long s = (long)Math.floor( seconds );
			long n = (long)((seconds - s) * 1e9);
			return Duration.ofSeconds( s, n );
		}

		public static double secondsFromDuration( Duration duration )
		{
			long s = duration.toSeconds();
			long n = duration.toNanosPart();
			return s + (n * 1e-9);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Debugging helpers (try-catch, try-finally, etc.)

	/**
	 * Performs a debugger-friendly {@code try-catch} and returns any caught {@link Throwable}.
	 *
	 * @param procedure the procedure to be invoked in the 'try' block.
	 */
	public static Optional<Throwable> tryCatch( Procedure0 procedure )
	{
		try
		{
			Debug.boundary( () -> procedure.invoke() );
			return Optional.empty();
		}
		catch( Throwable throwable )
		{
			return Optional.of( throwable );
		}
	}

	/**
	 * Performs a debugger-friendly {@code try-finally} that returns a result.
	 *
	 * @param tryFunction    the function to be invoked in the 'try' block.
	 * @param finallyHandler the handler to be invoked after the try-function.
	 * @param <R>            the type of the result.
	 *
	 * @return the result of the try-function.
	 */
	public static <R> R tryFinally( Function0<R> tryFunction, Procedure0 finallyHandler )
	{
		try
		{
			return Debug.boundary( () -> tryFunction.invoke() );
		}
		finally
		{
			finallyHandler.invoke();
		}
	}

	/**
	 * Performs a debugger-friendly {@code try-finally} that does not return a result.
	 *
	 * @param tryProcedure   the procedure to be invoked in the 'try' part.
	 * @param finallyHandler the handler to be invoked in the 'finally' part.
	 */
	public static void tryFinally( Procedure0 tryProcedure, Procedure0 finallyHandler )
	{
		try
		{
			Debug.boundary( () -> tryProcedure.invoke() );
		}
		finally
		{
			finallyHandler.invoke();
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

	//TODO: get rid of.
	public interface ThrowableThrowingFunction<R, E extends Throwable>
	{
		R invoke() throws E;
	}

	/**
	 * Invokes a given function declared with {@code throws Throwable}. TODO: get rid of.
	 * <p>
	 * PEARL: just to keep things interesting, Java does not only support checked exceptions, it even allows a method to be declared with
	 * {@code throws Throwable}, in which case the caller is forced to somehow do something about the declared {@code Throwable}, despite the fact that
	 * {@link Throwable} is not a checked exception. And sure enough, the JDK makes use of this weird feature in at least a few places that I am aware of, e.g.
	 * in {@code MethodHandle.invoke()} and {@code MethodHandle.invokeExact()}. The following method allows us to invoke methods declared with
	 * {@code throws Throwable} without having to handle or in any other way deal with the {@code Throwable}.
	 *
	 * @param function the {@link ThrowableThrowingFunction} to invoke.
	 * @param <R>      the type of result returned by the function.
	 * @param <E>      the type of throwable declared by the {@link ThrowableThrowingFunction}.
	 *
	 * @return the result of the function.
	 */
	public static <R, E extends Throwable> R invokeThrowingFunction( ThrowableThrowingFunction<R,E> function )
	{
		@SuppressWarnings( "unchecked" ) ThrowableThrowingFunction<R,RuntimeException> f = (ThrowableThrowingFunction<R,RuntimeException>)function;
		return f.invoke();
	}

	/**
	 * Performs a {@code try-with-resources} with Java's lame {@link AutoCloseable} interface whose {@link AutoCloseable#close()} method declares a checked
	 * exception.
	 */
	public static <R, C extends AutoCloseable> R uncheckedTryWith( C autoCloseable, Function1<R,C> tryFunction )
	{
		assert autoCloseable != null;
		try
		{
			return tryFunction.invoke( autoCloseable );
		}
		finally
		{
			unchecked( autoCloseable::close );
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

		public static boolean isInstanceMember( Member field )
		{
			return !isStaticMember( field );
		}

		public static boolean isStaticMember( Member field )
		{
			return Modifier.isStatic( field.getModifiers() );
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
		 * Naive Linear interpolation. (See std::lerp of C++20 for how much more complicated this could be)
		 */
		public static double interpolate( double a, double b, double t )
		{
			return a + t * (b - a);
		}

		/**
		 * Cubic interpolation. from <a href="https://www.paulinternet.nl/?page=bicubic">https://www.paulinternet.nl/?page=bicubic</a>
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
		 * Computes the remainder of a number according to IEEE 754. Essentially, this is supposed to be the same as the fmod() function of C++.
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

		/**
		 * Bhaskara I's sine approximation formula. This is SLOWER than Math.sin() by a factor of almost 2.
		 * <p>
		 * see <a href="https://en.wikipedia.org/wiki/Bhaskara_I%27s_sine_approximation_formula">Wikipedia: Bhaskara I's sine approximation formula</a>
		 */
		public static double sin_bhaskara1( double x )
		{
			double xx = x % π;
			double quadSine = (16 * xx * (π - xx)) / (5 * π * π - 4 * xx * (π - xx));
			return quadSine * Math.signum( π - x % (2 * π) );
		}

		/**
		 * Computes an approximation of sin(x) using Milian's method. This is accurate to about 1% and FASTER than Math.sin() by a factor of more than 2.
		 * <p>
		 * see <a href="https://stackoverflow.com/a/28050328/773113">https://stackoverflow.com/a/28050328/773113</a>.
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
		 * Computes an approximation of cos(x). using Milian's method. This is accurate to about 1% and FASTER than Math.cos() by a factor of more than 2.
		 * <p>
		 * see <a href="https://stackoverflow.com/a/28050328/773113">https://stackoverflow.com/a/28050328/773113</a>
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
		 * Computes an approximation of sin( x * 2π ) in other words: sin( x ) = sin2pi( x / 2π ) sin2pi( x ) = sin( x * 2π )
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
		 * Computes an approximation of cos( x * 2π ) in other words: cos( x ) = cos2pi( x / 2π ) cos2pi( x ) = cos( x * 2π )
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

		public static <T> T[] subArray( T[] array, int offset, int length )
		{
			assert 0 <= offset && offset < array.length;
			assert length >= 0;
			assert offset + length <= array.length;
			T[] newArray = newArrayLike( array, length );
			System.arraycopy( array, offset, newArray, 0, length );
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
			Class<T> elementClass = uncheckedClassCast( array.getClass().getComponentType() );
			return newArray( elementClass, length );
		}

		public static <T> T[] newArray( Class<T> elementClass, int length )
		{
			@SuppressWarnings( "unchecked" ) T[] result = (T[])Array.newInstance( elementClass, length );
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

		public interface BooleanFunction1Double
		{
			boolean invoke( double value );
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

	/**
	 * Allows throwing checked exceptions without having to declare them using the 'throws' keyword.  Incidentally, this also allows throwing instances of
	 * {@link Throwable}, which is a checked exception in java.
	 * <p>
	 * For more information, google "Sneaky throw".
	 * <p>
	 * Note: even though the method throws an exception, it is declared to also return an exception, which will never actually be returned. This allows the
	 * caller to invoke this method from within a {@code throw} statement, which, although unreachable, lets the compiler know that that execution will never
	 * proceed past that point.
	 */
	@SuppressWarnings( "unchecked" ) public static <T extends Throwable> RuntimeException sneakyException( Throwable t ) throws T
	{
		throw (T)t;
	}

	public static boolean isStatic( Member member )
	{
		int modifiers = member.getModifiers();
		return Modifier.isStatic( modifiers );
	}

	private static boolean mustBeValidMemberAssertion( Optional<Object> optionalTarget, Member member )
	{
		assert isStatic( member ) == optionalTarget.isEmpty();
		optionalTarget.ifPresent( target -> //
		{
			Class<?> declaringClass = member.getDeclaringClass();
			Class<?> targetClass = target.getClass();
			assert declaringClass.isAssignableFrom( targetClass );
		} );
		return true;
	}

	private static boolean mustBeValidMethodCallAssertion( Optional<Object> optionalTarget, Method method, Object[] arguments )
	{
		assert mustBeValidMemberAssertion( optionalTarget, method );
		assert method.getParameterCount() == arguments.length;
		return true;
	}

	private static <T> boolean mustBeValidConstructorCallAssertion( Constructor<T> constructor, Object[] arguments )
	{
		assert constructor.getParameterCount() == arguments.length;
		return true;
	}

	public static <T> T invokeInstanceMethod( Object target, Method method, Object... arguments )
	{
		return invokeMethod( Optional.of( target ), method, arguments );
	}

	public static <T> T invokeMethod( Optional<Object> target, Method method, Object... arguments )
	{
		assert mustBeValidMemberAssertion( target, method );
		//method.setAccessible( true );
		try
		{
			@SuppressWarnings( "unchecked" ) T result = (T)method.invoke( target.orElse( null ), arguments );
			return result;
		}
		catch( InvocationTargetException exception )
		{
			Throwable throwable = exception.getCause();
			if( throwable instanceof RuntimeException runtimeException )
				throw runtimeException;
			throw sneakyException( throwable );
		}
		catch( IllegalAccessException | IllegalArgumentException exception )
		{
			throw new RuntimeException( exception );
		}
	}

	/**
	 * Note: the documentation of Class.newInstance() gives the following justification for its deprecation:
	 * <p>
	 * "This method propagates any exception thrown by the nullary constructor, including a checked exception. Use of this method effectively bypasses the
	 * compile-time exception checking that would otherwise be performed by the compiler."
	 * <p>
	 * Yes, yes, that is precisely what we want!
	 */
	@SuppressWarnings( "deprecation" ) public static Object newInstance( Class<?> jvmClass )
	{
		return unchecked( jvmClass::newInstance );
	}

	public static <T> T newInstance( Constructor<T> constructor, Object... arguments )
	{
		assert mustBeValidConstructorCallAssertion( constructor, arguments );
		//method.setAccessible( true );
		try
		{
			return constructor.newInstance( arguments );
		}
		catch( InvocationTargetException exception )
		{
			Throwable throwable = exception.getCause();
			if( throwable instanceof RuntimeException runtimeException )
				throw runtimeException;
			throw sneakyException( throwable );
		}
		catch( IllegalAccessException | IllegalArgumentException exception )
		{
			throw new RuntimeException( exception );
		}
		catch( InstantiationException exception )
		{
			throw new UncheckedException( exception );
		}
	}

	////////////////////////////

	public static final class classLoading
	{
		public static InputStream getResourceAsStream( ClassLoader classLoader, String resourceName )
		{
			URL url = classLoader.getResource( resourceName );
			if( url == null )
			{
				Log.debug( "Could not load resource '" + resourceName + "' using classLoader " + classLoader );
				for( var s : troubleshoot( classLoader ) )
					Log.debug( "    " + s );
				assert false;
			}
			return unchecked( () -> url.openStream() );
		}

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
			troubleshootRecursively( mutableContents, classLoader );
			return mutableContents;
		}

		private static void troubleshootRecursively( ArrayList<String> mutableContents, ClassLoader classLoader )
		{
			ClassLoader parentClassLoader = classLoader.getParent();
			if( parentClassLoader != null )
				troubleshootRecursively( mutableContents, parentClassLoader );

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
		public static <T> void print( T rootNode, Function1<Iterable<? extends T>,T> breeder, Function1<String,T> stringizer, Procedure1<String> emitter )
		{
			StringBuilder stringBuilder = new StringBuilder();
			printTreeRecursive( stringBuilder, "", rootNode, "", breeder, stringizer, emitter );
		}

		private static final String midLeaf = "├─";
		private static final String endLeaf = "└─";
		private static final String midNode = "│ ";
		private static final String endNode = "  ";
		private static final String terminal = "■ ";

		private static <T> void printTreeRecursive( StringBuilder stringBuilder, String parentPrefix, T node, String childPrefix, Function1<Iterable<? extends T>,T> breeder, Function1<String,T> stringizer, Procedure1<String> emitter )
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

	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Synchronization

	public static final class sync
	{
		public static <T> void assertAndSet( AtomicReference<T> atomicReference, T expectedValue, T newValue )
		{
			boolean ok = atomicReference.compareAndSet( expectedValue, newValue );
			assert ok;
		}

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

		public static void lock( Lock lock, Procedure0 procedure )
		{
			lock.lock();
			try
			{
				Debug.boundary( () -> procedure.invoke() );
			}
			finally
			{
				lock.unlock();
			}
		}

		public static <T> T lock( Lock lock, Function0<T> function )
		{
			lock.lock();
			try
			{
				return Debug.boundary( () -> function.invoke() );
			}
			finally
			{
				lock.unlock();
			}
		}

		public static <R> R postAndWait( Procedure1<Procedure0> poster, Function0<R> function0 )
		{
			Ref<R> resultRef = Ref.of();
			CountDownLatch countDownLatch = new CountDownLatch( 1 );
			poster.invoke( () -> //
			{
				resultRef.value = function0.invoke();
				countDownLatch.countDown();
			} );
			for( ; ; )
			{
				if( unchecked( () -> countDownLatch.await( postAndWaitTimeout.toMillis(), TimeUnit.MILLISECONDS ) ) )
					break;
				Log.debug( "function did not complete within " + time.secondsFromDuration( postAndWaitTimeout ) + " s." );
				Debug.breakPoint();
			}
			assert countDownLatch.getCount() == 0;
			return resultRef.value;
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

	public static <R, S> Optional<R> tryAs( Class<R> jvmClass, S instance )
	{
		if( jvmClass.isInstance( instance ) )
			return Optional.of( jvmClass.cast( instance ) );
		return Optional.empty();
	}

	/**
	 * Checks whether two objects are equal by value.
	 * <p>
	 * Unlike the {@link Objects#equals(Object, Object)} method, this method uses generics to make sure at compile time that the objects being compared are
	 * indeed of compatible types.  Also, at runtime, it asserts the same thing.
	 * <p>
	 * An example of a case where this is useful is when comparing a java.util.Date and a {@link java.sql.Timestamp}: Objects.equals( utilDate, sqlTimestamp )
	 * will work, but Objects.equals( sqlTimestamp, utilDate ) will silently yield false negatives. (A nasty bug to track down.)
	 * <p>
	 * With this method, equalByValue( utilDate, sqlTimestamp ) will work, equalByValue( sqlTimestamp, utilDate ) will not compile, and equalByValue(
	 * (java.util.Date)sqlTimestamp, utilDate ) will result in an assertion failure instead of silently yielding false negatives.
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
		final Class<T> type;
		final Class<T> wrapperClass;
		final T defaultInstance;

		private PrimitiveInfo( Class<T> type, Class<T> wrapperClass, T defaultInstance )
		{
			this.type = type;
			this.wrapperClass = wrapperClass;
			this.defaultInstance = defaultInstance;
		}
	}

	private static final List<PrimitiveInfo<?>> primitiveTypeInfo = List.of( //
		new PrimitiveInfo<>( boolean.class /**/, Boolean.class   /**/, false ), //
		new PrimitiveInfo<>( char.class    /**/, Character.class /**/, '\0' ), //
		new PrimitiveInfo<>( byte.class    /**/, Byte.class      /**/, (byte)0 ), //
		new PrimitiveInfo<>( short.class   /**/, Short.class     /**/, (short)0 ), //
		new PrimitiveInfo<>( int.class     /**/, Integer.class   /**/, 0 ), //
		new PrimitiveInfo<>( long.class    /**/, Long.class      /**/, 0L ), //
		new PrimitiveInfo<>( float.class   /**/, Float.class     /**/, 0f ), //
		new PrimitiveInfo<>( double.class  /**/, Double.class    /**/, 0d ), //
		new PrimitiveInfo<>( void.class    /**/, Void.class      /**/, null ) );

	private static <T> PrimitiveInfo<T> findPrimitiveInfo( Predicate<PrimitiveInfo<?>> predicate )
	{
		for( PrimitiveInfo<?> primitiveInfo : primitiveTypeInfo )
			if( predicate.test( primitiveInfo ) )
			{
				@SuppressWarnings( "unchecked" ) PrimitiveInfo<T> result = (PrimitiveInfo<T>)primitiveInfo;
				return result;
			}
		return null;
	}

	private static <T> PrimitiveInfo<T> findPrimitiveInfoByPrimitiveType( Class<?> primitiveType )
	{
		return findPrimitiveInfo( primitiveInfo -> primitiveInfo.type == primitiveType );
	}

	private static <T> PrimitiveInfo<T> findPrimitiveInfoByPrimitiveWrapperClass( Class<?> primitiveWrapperClass )
	{
		return findPrimitiveInfo( primitiveInfo -> primitiveInfo.wrapperClass == primitiveWrapperClass );
	}

	private static <T> PrimitiveInfo<T> findPrimitiveInfoByPrimitiveName( String primitiveName )
	{
		return findPrimitiveInfo( primitiveInfo -> primitiveInfo.type.getName().equals( primitiveName ) );
	}

	private static <T> PrimitiveInfo<T> findPrimitiveInfoByPrimitiveWrapperClassName( String primitiveWrapperClassName )
	{
		return findPrimitiveInfo( primitiveInfo -> primitiveInfo.wrapperClass.getName().equals( primitiveWrapperClassName ) );
	}

//	private static int indexOfPrimitiveType( Class<?> clazz )
//	{
//		int n = primitiveTypeInfo.size();
//		for( int i = 0; i < n; i++ )
//			if( clazz == primitiveTypeInfo.get( i ).primitiveClass )
//				return i;
//		return -1;
//	}
//
//	private static int indexOfPrimitiveWrapperClass( Class<?> clazz )
//	{
//		int n = primitiveTypeInfo.size();
//		for( int i = 0; i < n; i++ )
//			if( clazz == primitiveTypeInfo.get( i ).wrapperClass )
//				return i;
//		return -1;
//	}

	/**
	 * Checks whether a class is a primitive wrapper type.
	 *
	 * @param clazz the class to check.
	 *
	 * @return {@code true} if the class is a primitive wrapper type. (One of Boolean, Character, Byte, Short, Integer, Long, Float, Double and Void.)
	 */
	public static boolean isPrimitiveWrapperClass( Class<?> clazz )
	{
		return findPrimitiveInfoByPrimitiveWrapperClass( clazz ) != null;
	}

	/**
	 * Gets all java primitive types.
	 */
	public static Collection<Class<?>> getAllPrimitives()
	{
		return primitiveTypeInfo.stream().<Class<?>>map( i -> i.type ).toList();
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
	 * @param primitiveType the primitive type whose wrapper is requested.
	 *
	 * @return the class of the wrapper for the given primitive type.
	 */
	public static <T> Class<T> getPrimitiveWrapperClassByPrimitiveType( Class<T> primitiveType )
	{
		PrimitiveInfo<T> primitiveInfo = findPrimitiveInfoByPrimitiveType( primitiveType );
		assert primitiveInfo != null;
		return primitiveInfo.wrapperClass;
	}

	public static <T> T getDefaultPrimitiveWrapperInstance( Class<T> primitiveWrapperClass )
	{
		PrimitiveInfo<T> primitiveInfo = findPrimitiveInfoByPrimitiveWrapperClass( primitiveWrapperClass );
		return primitiveInfo.defaultInstance;
	}

	public static Class<?> getPrimitiveTypeByName( String name )
	{
		PrimitiveInfo<?> primitiveInfo = findPrimitiveInfoByPrimitiveName( name );
		assert primitiveInfo != null;
		return primitiveInfo.type;
	}

	/**
	 * Performs an assertion, and if it fails, wraps the generated throwable in a new throwable created by a given factory.
	 * <p>
	 * Necessary for situations where we want to chain assertions (`assert someOtherAssertion()`) and also wrap the thrown exceptions, because the `assert`
	 * keyword does not expect the assertion-expression to throw, so it does not try to catch any exception thrown by it. Thus, the construct `assert
	 * someOtherAssertion() : new SomeNewException();` does not work: the exception thrown within `someOtherAssertion()` will not be wrapped with
	 * SomeNewException.
	 * <p>
	 * With this function, we can say `assert Kit.assertion( nestedAssertion, cause -> new SomeNewException( cause ) ); return true;`
	 */
	public static boolean assertion( BooleanFunction0 nestedAssertion, Function1<Throwable,Throwable> throwableFactory )
	{
		try
		{
			assert nestedAssertion.invoke();
			return true;
		}
		catch( Throwable throwable )
		{
			while( throwable instanceof AssertionError assertionError && assertionError.getCause() != null )
				throwable = assertionError.getCause();
			throwable = throwableFactory.invoke( throwable );
			throw new AssertionError( throwable );
		}
	}

	public static Throwable mapAssertionErrorToCause( Throwable throwable )
	{
		while( throwable instanceof AssertionError && throwable.getCause() != null )
			throwable = throwable.getCause();
		return throwable;
	}
}
