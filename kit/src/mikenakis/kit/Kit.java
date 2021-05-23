package mikenakis.kit;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
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

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@SuppressWarnings( { "unused", "NewClassNamingConvention" } )
public final class Kit
{
	public static final byte[] ARRAY_OF_ZERO_BYTES = new byte[0];
	public static final Object[] ARRAY_OF_ZERO_OBJECTS = new Object[0];

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
	public static <T> T fail()
	{
		throw new AssertionError();
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
	 * Useful because {@link Object#getClass()} returns a generic class parametrized with a wildcard, not with the actual type of the object on which getClass() was called.
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

	public static String stringFromThrowable( Throwable throwable )
	{
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter( stringWriter );
		throwable.printStackTrace( printWriter );
		return stringWriter.toString();
	}

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

		public static void appendEscapedForJava( StringBuilder stringBuilder, String s )
		{
			if( s == null )
			{
				stringBuilder.append( "null" );
				return;
			}
			stringBuilder.append( '"' );
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
			stringBuilder.append( '"' );
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
			if( object == null )
			{
				stringBuilder.append( "null" );
				return;
			}

			/* if the object is a string, append its value surrounded in quotes and escaped. */
			if( object.getClass() == String.class )
			{
				String s = (String)object;
				appendEscapedForJava( stringBuilder, s );
				return;
			}

			/* otherwise, invoke toString() on the object. */
			String s = object.toString();
			stringBuilder.append( s );
		}
	}

	public static class string
	{
		/**
		 * Splits a string in parts using a given character as delimiter.
		 * Corrects Java's insanity of only offering versions of this function that work with regular expressions.
		 *
		 * @param stringToSplit the string to split.
		 * @param delimiter     the delimiter.
		 *
		 * @return A {@link List} of {@link String}s. If the delimiter is not found, the list will contain a single element, which will be the entire source string.
		 */
		public static List<String> splitAtCharacter( String stringToSplit, char delimiter )
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
		 * @return A {@link List} of {@link String}s. If the delimiter is not found, the list will contain a single element, which will be the entire source string.
		 */
		public static List<String> splitAtCharacter( String stringToSplit, char delimiter, boolean includeDelimiter )
		{
			List<String> result = new ArrayList<>();
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
			return result;
		}

		/**
		 * Build a string.
		 */
		public static <E> String make( String delimiter, Iterable<E> iterable )
		{
			StringBuilder stringBuilder = new StringBuilder();
			boolean first = true;
			for( E element : iterable )
			{
				if( first )
					first = false;
				else
					stringBuilder.append( delimiter );
				stringBuilder.append( element );
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
							ch = '\\';
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
			stringBuilder.appendEscapedForJava( builder, s );
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
	// Java Collection<T>

	public static final class collection
	{
		public static final class stream
		{
			/***
			 * Obtains a {@link Stream} from an {@link Iterable}.
			 * Because Java makes it awfully difficult, whereas it should have been so easy as to not even require a cast. (Ideally, Stream would extend Iterable. I
			 * know, it can't. But ideally, it would.)
			 */
			public static <T> Stream<T> streamFromIterable( Iterable<T> iterable )
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
		 * Corresponds to Java's {@link Collection#add(T)}, except that it corrects Java's deplorable dumbfuckery of not throwing an exception when the item already exists.
		 */
		@SuppressWarnings( "deprecation" ) public static <T> void add( Collection<T> collection, T item )
		{
			assert item != null;
			boolean ok = collection.add( item );
			assert ok;
		}

		/**
		 * Adds an item to a {@link Collection} if it does not already exist.
		 * Corresponds to Java's {@link Collection#add(T)}.
		 *
		 * @return {@code true} if the item was added; {@code false} if the item was not added because it already existed.
		 */
		@SuppressWarnings( "deprecation" ) public static <T> boolean tryAdd( Collection<T> collection, T item )
		{
			return collection.add( item );
		}

		/**
		 * Removes an item from a {@link Collection}. The item must already exist.
		 * Corresponds to Java's {@link Collection#remove(T)}, except that it corrects Java's deplorable dumbfuckery of not throwing an exception when the item does not exist.
		 */
		@SuppressWarnings( "deprecation" ) public static <T> void remove( Collection<T> collection, T item )
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
		@SuppressWarnings( "deprecation" ) public static <T> boolean tryRemove( Collection<? extends T> collection, T item )
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

		public static <T> boolean containsAny( Collection<T> a, Collection<T> b )
		{
			for( T element : b )
				if( a.contains( element ) )
					return true;
			return false;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Java Map<K,V>

	public static final class map
	{
		/**
		 * Gets a value by key from a {@link Map}. The key must exist.
		 * Corresponds to Java's {@link Map#get(K)}, except that it corrects Java's deplorable dumbfuckery of not throwing an exception when the key does not exist.
		 */
		@SuppressWarnings( "deprecation" ) public static <K, V> V get( Map<K,V> map, K key )
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
		@SuppressWarnings( "deprecation" ) public static <K, V> V tryGet( Map<? extends K,V> map, K key )
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
		 * Corresponds to Java's {@link Map#put(K, V)}, except that it corrects Java's deplorable dumbfuckery of not throwing an exception when the key already exists.
		 */
		@SuppressWarnings( "deprecation" ) public static <K, V> void add( Map<K,V> map, K key, V value )
		{
			assert key != null;
			assert value != null;
			Object previous = map.put( key, value );
			assert previous == null : key + " " + value;
		}

		/**
		 * Removes a key-value pair from a {@link Map}. The key must already exist.
		 * Corresponds to Java's {@link Map#remove(K)}, except that it corrects Java's deplorable dumbfuckery of not throwing an exception when the key does not exist.
		 */
		@SuppressWarnings( "deprecation" ) public static <K, V> V remove( Map<K,V> map, K key )
		{
			V previous = map.remove( key );
			assert previous != null;
			return previous;
		}

		/**
		 * Tries to remove a key-value pair from a {@link Map}.
		 * Corresponds to Java's {@link Map#remove(K)}, but with a name that documents exactly what it does.
		 *
		 * @return the value that was previously associated with the given key, or {@code null} if the key was not in the map.
		 */
		@SuppressWarnings( "deprecation" ) public static <K, V> V tryRemove( Map<? extends K,V> map, K key )
		{
			return map.remove( key );
		}

		/**
		 * Removes a key from a {@link Map}, if the key is present. The key may and may not already exist.
		 * Corresponds to Java's {@link Map#remove(K)}, the difference being that by using this method we are documenting the fact that we are intentionally
		 * allowing the key to potentially not exist.
		 */
		@SuppressWarnings( "deprecation" ) public static <K, V> void removeIfPresent( Map<K,V> map, K key )
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
		@SuppressWarnings( "deprecation" ) public static <K, V> boolean tryAdd( Map<K,V> map, K key, V value )
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
		@SuppressWarnings( "deprecation" ) public static <K, V> boolean tryReplace( Map<K,V> map, K key, V value )
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
		 * Corresponds to Java's {@link Map#put(K, V)}, except that it corrects Java's deplorable dumbfuckery of not throwing an exception when the key does not exist.
		 */
		@SuppressWarnings( "deprecation" ) public static <K, V> void replace( Map<K,V> map, K key, V value )
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
		@SuppressWarnings( "deprecation" ) public static <K, V> boolean addOrReplace( Map<K,V> map, K key, V value )
		{
			assert key != null;
			assert value != null;
			Object old = map.put( key, value );
			return old != null;
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
	 *     <li><p>As a bonus, it also avoids Java's deplorable dumbfuckery of forcing you in the case of {@code try-finally} to use curly braces
	 *     even when the code blocks consist of single statements. (You supply the {@code try} code and the {@code finally} code in lambdas,
	 *     so you get to decide whether to use curly braces or not.)</p></li>
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
	 *     <li><p>As a bonus, it also avoids Java's deplorable dumbfuckery of forcing you in the case of {@code try-finally} to use curly braces
	 *     even when the code blocks consist of single statements. (You supply the {@code try} code and the {@code finally} code in lambdas,
	 *     so you get to decide whether to use curly braces or not.)</p></li>
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
	 *     <li><p>As a bonus, it also avoids Java's deplorable dumbfuckery of forcing you in the case of {@code try-with-resources} to use curly braces
	 *     even when the code block consists of a single-statement. (You supply the {@code try} code in a lambda, so you get to decide whether to use
	 *     curly braces or not.)</p></li>
	 * </ul>
	 *
	 * @param closeable   the {@link Closeable} to close when done.
	 * @param tryFunction a function which receives the {@link Closeable} object and produces a result.
	 * @param <C>         the type of the {@link Closeable}. (Must extend {@link Closeable}.)
	 * @param <R>         the type of the result.
	 *
	 * @return the result of the try-function.
	 */
	public static <C extends Closeable, R> R tryWithResources( C closeable, Function1<R,? super C> tryFunction )
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
	 * Same as {@link #tryWithResources(C, Function1)} but with a {@link Function0}.
	 * Avoids Java's deplorable dumbfuckery of forcing you to declare a variable for the closeable, even when you have no use for it.
	 *
	 * @param closeable   the {@link Closeable} to close when done.
	 * @param tryFunction a function which produces a result.
	 * @param <C>         the type of the {@link Closeable}. (Must extend {@link Closeable}.)
	 * @param <R>         the type of the result.
	 *
	 * @return the result of the try-function.
	 */
	public static <C extends Closeable, R> R tryWithResources( C closeable, Function0<R> tryFunction )
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
	 *     <li><p>As a bonus, it also avoids Java's deplorable dumbfuckery of forcing you in the case of {@code try-with-resources} to use curly braces
	 *     even when the code block consists of a single-statement. (You supply the {@code try} code in a lambda, so you get to decide whether to use
	 *     curly braces or not.)</p></li>
	 * </ul>
	 *
	 * @param closeable    the {@link Closeable} to close when done.
	 * @param tryProcedure a {@link Procedure1} which receives the closeable object and does something with it.
	 * @param <C>          the type of the {@link Closeable}. (Must extend {@link Closeable}.)
	 */
	public static <C extends Closeable> void tryWithResources( C closeable, Procedure1<? super C> tryProcedure )
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
	 * Same as {@link #tryWithResources(C, Procedure1)} but with a {@link Procedure0}.
	 * Avoids Java's deplorable dumbfuckery of forcing you to declare a variable for the closeable, even when you have no use for it.
	 *
	 * @param closeable    the {@link Closeable} to close when done.
	 * @param tryProcedure the {@link Procedure0} to execute.
	 * @param <C>          the type of the {@link Closeable}. (Must extend {@link Closeable}.)
	 */
	public static <C extends Closeable> void tryWithResources( C closeable, Procedure0 tryProcedure )
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
	 * This method allows us to invoke methods declared with {@code throws Throwable} without having to do anything about the {@code Throwable}.
	 *
	 * @param throwingThrowableFunction the {@link ThrowableThrowingFunction} to invoke.
	 * @param <R>                       the type of result returned by the function.
	 * @param <E>                       the type of throwable declared by the {@link ThrowableThrowingFunction}.
	 *
	 * @return the result of the function.
	 */
	public static <R, E extends Throwable> R invokeThrowableThrowingFunction( ThrowableThrowingFunction<R,E> throwingThrowableFunction )
	{
		@SuppressWarnings( "unchecked" ) ThrowableThrowingFunction<R,RuntimeException> f = (ThrowableThrowingFunction<R,RuntimeException>)throwingThrowableFunction;
		return f.invoke();
	}

	/**
	 * Performs a {@code try-with-resources} with Java's lame {@link AutoCloseable} interface whose {@link AutoCloseable#close()} method declares a checked
	 * exception.
	 */
	public static <C extends AutoCloseable, R, E extends Exception> R uncheckedTryWithResources( ThrowingFunction0<C,E> closeableFactory, //
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
		 * Creates a new array, from a given array plus an element appended at the end, using a given {@link EqualityComparator}. The element must not already exist.
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
		 * Creates a new array, from a given array plus an element inserted at a given index, using a given {@link EqualityComparator}. The element must not already exist.
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
		 * Adapted from <a href="http://stackoverflow.com/q/1481178/773113">Stack Overflow: Forcing Garbage Collection in Java?</a>
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
	}

	/**
	 * For information about this method, google "Sneaky throw".
	 */
	@SuppressWarnings( "unchecked" ) public static <T extends Throwable> RuntimeException sneakyException( Throwable t ) throws T
	{
		throw (T)t;
	}
}
