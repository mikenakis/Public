package mikenakis.kit.buffer;

import mikenakis.kit.Dyad;
import mikenakis.kit.Kit;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

/**
 * A Buffer. Similar to {@link String}, but containing bytes instead of characters.
 *
 * @author michael.gr
 */
//@Immutable
public final class Buffer implements Comparable<Buffer>
{
	public static final Buffer EMPTY = new Buffer( new byte[0] );

	//@Stable
	private final byte[] bytes;
	//@Stable
	private int lazyHashCode = 0;

	private Buffer( byte[] content )
	{
		bytes = content;
	}

	public static Buffer of( byte content )
	{
		byte[] bytes = new byte[1];
		bytes[0] = content;
		return new Buffer( bytes );
	}

	public static Buffer of( char content )
	{
		byte[] bytes = new byte[1];
		bytes[0] = (byte)content;
		return new Buffer( bytes );
	}

	public static Buffer of( String content )
	{
		assert content != null : new NullPointerException();
		if( content.isEmpty() )
			return EMPTY;
		byte[] bytes = content.getBytes( StandardCharsets.UTF_8 );
		return new Buffer( bytes );
	}

	public static Buffer of( String content, Charset charset )
	{
		assert content != null : new NullPointerException();
		if( content.isEmpty() )
			return EMPTY;
		byte[] bytes = content.getBytes( charset );
		return new Buffer( bytes );
	}

	public static Buffer of( char[] content )
	{
		assert content != null : new NullPointerException();
		if( content.length == 0 )
			return EMPTY;
		byte[] bytes = new String( content ).getBytes( StandardCharsets.UTF_8 );
		return new Buffer( bytes );
	}

	public static Buffer of( char[] content, Charset charset )
	{
		assert content != null : new NullPointerException();
		if( content.length == 0 )
			return EMPTY;
		byte[] bytes = new String( content ).getBytes( charset );
		return new Buffer( bytes );
	}

	public static Buffer of( byte[] content )
	{
		assert content != null : new NullPointerException();
		if( content.length == 0 )
			return EMPTY;
		byte[] ownBytes = new byte[content.length];
		System.arraycopy( content, 0, ownBytes, 0, content.length );
		return new Buffer( ownBytes );
	}

	public static Buffer of( byte[] content, int offset, int length )
	{
		assert content != null : new NullPointerException();
		assert offset >= 0;
		assert length >= 0;
		assert offset + length <= content.length;
		if( length == 0 )
			return EMPTY;
		byte[] ownBytes = new byte[length];
		System.arraycopy( content, offset, ownBytes, 0, length );
		return new Buffer( ownBytes );
	}

	public byte byteAt( int index )
	{
		return bytes[index];
	}

	public int getLength()
	{
		return bytes.length;
	}

	public boolean startsWith( Buffer buffer )
	{
		if( buffer.getLength() > bytes.length )
			return false;
		return Kit.bytes.compare( bytes, 0, buffer.bytes, 0, buffer.getLength() ) == 0;
	}

	public boolean endsWith( Buffer buffer )
	{
		if( buffer.getLength() > bytes.length )
			return false;
		return Kit.bytes.compare( bytes, bytes.length - buffer.getLength(), buffer.bytes, 0, buffer.getLength() ) == 0;
	}

	@Override public int compareTo( Buffer o )
	{
		int commonLength = Math.min( bytes.length, o.bytes.length );
		int d = Kit.bytes.compare( bytes, 0, o.bytes, 0, commonLength );
		if( d != 0 )
			return d;
		return Integer.compare( bytes.length, o.bytes.length );
	}

	@Override public boolean equals( Object o )
	{
		if( this == o )
			return true;
		if( o == null || getClass() != o.getClass() )
			return false;

		Buffer buffer = (Buffer)o;

		if( !Arrays.equals( bytes, buffer.bytes ) )
			return false;

		return true;
	}

	@SuppressWarnings( "NonFinalFieldReferencedInHashCode" ) @Override public int hashCode()
	{
		if( lazyHashCode == 0 )
			lazyHashCode = Arrays.hashCode( bytes );
		return lazyHashCode;
	}

	public byte[] getBytes()
	{
		return Arrays.copyOf( bytes, bytes.length );
	}

	public void copyBytes( byte[] destination )
	{
		copyBytes( 0, destination, 0, bytes.length );
	}

	public void copyBytes( byte[] destination, int destinationOffset )
	{
		copyBytes( 0, destination, destinationOffset, bytes.length );
	}

	public void copyBytes( int sourceOffset, byte[] destination, int length )
	{
		copyBytes( sourceOffset, destination, 0, length );
	}

	public void copyBytes( byte[] destination, int destinationOffset, int length )
	{
		copyBytes( 0, destination, destinationOffset, length );
	}

	public void copyBytes( int sourceOffset, byte[] destination, int destinationOffset, int length )
	{
		System.arraycopy( bytes, sourceOffset, destination, destinationOffset, length );
	}

	public static Buffer valueOf( Object object )
	{
		String s = String.valueOf( object );
		return of( s );
	}

	@Override public String toString()
	{
		return new String( bytes, StandardCharsets.UTF_8 );
	}

	public String toString( Charset charset )
	{
		return new String( bytes, charset );
	}

//    public int indexOf(int ch)
//    public int indexOf(int ch, int fromIndex)
//    public int indexOf(String str)
//    public int indexOf(String str, int fromIndex)
//    int i = new String().lastIndexOf();

	public int indexOf( byte value )
	{
		return Kit.bytes.indexOf( bytes, 0, getLength(), value );
	}

	public int indexOf( byte value, int fromIndex )
	{
		return Kit.bytes.indexOf( bytes, fromIndex, getLength(), value );
	}

	public int lastIndexOf( byte value )
	{
		return Kit.bytes.lastIndexOf( bytes, 0, getLength(), value );
	}

	public int lastIndexOf( byte value, int fromIndex )
	{
		return Kit.bytes.lastIndexOf( bytes, fromIndex, getLength(), value );
	}

	public int indexOf( Buffer pattern )
	{
		return Kit.bytes.indexOf( bytes, 0, getLength(), pattern.bytes );
	}

	public int indexOf( Buffer pattern, int fromIndex )
	{
		return Kit.bytes.indexOf( bytes, fromIndex, getLength() - fromIndex, pattern.bytes );
	}

	public int lastIndexOf( Buffer pattern )
	{
		return Kit.bytes.lastIndexOf( bytes, getLength(), getLength(), pattern.bytes );
	}

	public int lastIndexOf( Buffer pattern, int fromIndex )
	{
		return Kit.bytes.lastIndexOf( bytes, fromIndex, fromIndex, pattern.bytes );
	}

	public static int indexOf( byte[] bytes, Buffer pattern )
	{
		return Kit.bytes.indexOf( bytes, 0, bytes.length, pattern.bytes );
	}

	public static int lastIndexOf( byte[] bytes, Buffer pattern )
	{
		return Kit.bytes.lastIndexOf( bytes, 0, bytes.length, pattern.bytes );
	}

	public static int indexOf( byte[] bytes, int index, int count, Buffer pattern )
	{
		return Kit.bytes.indexOf( bytes, index, count, pattern.bytes );
	}

	public static int lastIndexOf( byte[] bytes, int index, int count, Buffer pattern )
	{
		return Kit.bytes.lastIndexOf( bytes, index, count, pattern.bytes );
	}

	public int getCaseInsensitiveHashCode()
	{
		int h = 0;
		for( byte v : bytes )
		{
			v = toUpper( v );
			h = 31 * h + (v & 0xff);
		}
		return h;
	}

	public static byte toUpper( byte b )
	{
		if( b >= 'a' && b <= 'z' )
			b -= 32;
		return b;
	}

	public boolean equalsIgnoreCase( Buffer other )
	{
		return CaseInsensitiveBufferEqualityComparator.INSTANCE.equals( this, other );
	}

	public boolean isEmpty()
	{
		return getLength() == 0;
	}

	public Buffer subBuffer( int beginIndex )
	{
		return subBuffer( beginIndex, getLength() );
	}

	public Buffer subBuffer( int beginIndex, int endIndex )
	{
		assert beginIndex >= 0;
		assert endIndex <= getLength();
		if( beginIndex == 0 && endIndex == getLength() )
			return this;
		return of( bytes, beginIndex, endIndex - beginIndex );
	}

	/**
	 * Splits the {@link Buffer} on a given delimiter.
	 *
	 * @param delimiter the delimiter to split on.
	 * @param trim      whether to trim whitespace from the generated strings.
	 *
	 * @return an Iterable which yields the parts.
	 */
	public Buffer[] split( Buffer delimiter, boolean trim )
	{
		assert delimiter != null : new NullPointerException();
		assert !delimiter.isEmpty() : new IllegalArgumentException();
		Collection<Buffer> list = new ArrayList<>();
		boolean first = true;
		for( int position = 0; position < getLength(); )
		{
			if( first )
				first = false;
			else
			{
				assert subBuffer( position, position + delimiter.getLength() ).equals( delimiter );
				position += delimiter.getLength();
			}
			if( trim )
				position = skipWhitespaceForward( position, getLength() );
			int i = position;
			position = indexOf( delimiter, i );
			if( position == -1 )
				position = getLength();
			int k = position;
			if( trim )
				k = skipWhitespaceBackward( i, k );
			list.add( subBuffer( i, k ) );
		}
		return list.toArray( Buffer[]::new );
	}

	/**
	 * Splits the {@link Buffer} on a given delimiter.
	 *
	 * @param delimiter the delimiter to split on.
	 *
	 * @return an Iterable which yields the parts.
	 */
	public Buffer[] split( Buffer delimiter )
	{
		return split( delimiter, false );
	}

	/**
	 * Splits the {@link Buffer} on a given delimiter.
	 *
	 * @param delimiter the delimiter to split on.
	 * @param trim      whether to trim whitespace from the generated strings.
	 *
	 * @return an Iterable which yields the parts of the String.
	 */
	public Buffer[] split( byte delimiter, boolean trim )
	{
		Buffer delimiterAsBuffer = of( delimiter );
		return split( delimiterAsBuffer, trim );
	}

	/**
	 * Splits the {@link Buffer} on a given delimiter.
	 *
	 * @param delimiter the delimiter to split on.
	 *
	 * @return an Iterable which yields the parts of the String.
	 */
	public Buffer[] split( byte delimiter )
	{
		Buffer delimiterAsBuffer = of( delimiter );
		return split( delimiterAsBuffer, false );
	}

	public Dyad<Buffer,Optional<Buffer>> splitInTwo( byte delimiter, boolean trim )
	{
		return splitInTwo( of( delimiter ), trim );
	}

	/**
	 * Splits the {@link Buffer} in two parts on a given delimiter.  A part may be empty if the {@link Buffer} starts or ends with the delimiter. The second part will be {@code
	 * null} if the {@link Buffer} did not contain the delimiter.
	 *
	 * @param delimiter the delimiter to split on.
	 * @param trim      whether white should be trimmed from each part.
	 *
	 * @return a {@link Dyad} containing the parts on either side of the delimiter.
	 */
	public Dyad<Buffer,Optional<Buffer>> splitInTwo( Buffer delimiter, boolean trim )
	{
		assert !delimiter.isEmpty();
		assert !(trim && delimiter.isWhitespace());

		int h = 0;
		int k = bytes.length;
		if( trim )
		{
			h = skipWhitespaceForward( h, k );
			k = skipWhitespaceBackward( h, k );
		}

		int i = indexOf( delimiter, h );
		if( i == -1 || i >= k )
		{
			Buffer firstPart = subBuffer( h, k );
			return new Dyad<>( firstPart, Optional.empty() );
		}

		int j = i + delimiter.bytes.length;

		if( trim )
		{
			i = skipWhitespaceBackward( h, i );
			j = skipWhitespaceForward( j, k );
		}

		return new Dyad<>( subBuffer( h, i ), Optional.of( subBuffer( j, k ) ) );
	}

	public Buffer trim()
	{
		int start = skipWhitespaceForward( 0, bytes.length );
		int end = skipWhitespaceBackward( start, bytes.length );
		return subBuffer( start, end );
	}

	public boolean isWhitespace()
	{
		for( byte b : bytes )
			if( !Kit.bytes.isWhitespace( b ) )
				return false;
		return true;
	}

	public int skipWhitespaceForward()
	{
		return skipWhitespaceForward( 0, bytes.length );
	}

	public int skipWhitespaceForward( int end )
	{
		return skipWhitespaceForward( 0, end );
	}

	public int skipWhitespaceForward( int start, int end )
	{
		while( start < end && Kit.bytes.isWhitespace( bytes[start] ) )
			start++;
		return start;
	}

	public int skipWhitespaceBackward( int start )
	{
		return skipWhitespaceBackward( start, bytes.length );
	}

	public int skipWhitespaceBackward( int start, int end )
	{
		while( end > start && Kit.bytes.isWhitespace( bytes[end - 1] ) )
			end--;
		return end;
	}

	public Buffer concat( Buffer other )
	{
		if( other.bytes.length == 0 )
			return this;
		if( bytes.length == 0 )
			return other;
		byte[] newBytes = new byte[bytes.length + other.bytes.length];
		System.arraycopy( bytes, 0, newBytes, 0, bytes.length );
		System.arraycopy( other.bytes, 0, newBytes, bytes.length, other.bytes.length );
		return new Buffer( newBytes );
	}

	public void copyTo( int arrayOffset, byte[] destinationBytes, int offset, int length )
	{
		System.arraycopy( bytes, arrayOffset, destinationBytes, offset, length );
	}
}
