package io.github.mikenakis.buffer;

import io.github.mikenakis.kit.Kit;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * A Buffer. Similar to {@link String}, but containing bytes instead of characters.
 *
 * @author michael.gr
 */
//@Immutable
public final class Buffer implements Comparable<Buffer>
{
	private static final byte[] arrayOfZeroBytes = new byte[0];
	public static final Buffer EMPTY = new Buffer( arrayOfZeroBytes );

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
		return of( content, StandardCharsets.UTF_8 );
	}

	public static Buffer of( String content, Charset charset )
	{
		if( content.isEmpty() )
			return EMPTY;
		byte[] bytes = content.getBytes( charset );
		return new Buffer( bytes );
	}

	public static Buffer of( char[] content )
	{
		if( content.length == 0 )
			return EMPTY;
		byte[] bytes = new String( content ).getBytes( StandardCharsets.UTF_8 );
		return new Buffer( bytes );
	}

	public static Buffer of( char[] content, Charset charset )
	{
		if( content.length == 0 )
			return EMPTY;
		byte[] bytes = new String( content ).getBytes( charset );
		return new Buffer( bytes );
	}

	public static Buffer of( byte[] content )
	{
		if( content.length == 0 )
			return EMPTY;
		byte[] ownBytes = new byte[content.length];
		System.arraycopy( content, 0, ownBytes, 0, content.length );
		return new Buffer( ownBytes );
	}

	public static Buffer of( byte[] content, int offset, int length )
	{
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

	public int size()
	{
		return bytes.length;
	}

	public boolean startsWith( Buffer buffer )
	{
		if( buffer.size() > bytes.length )
			return false;
		return Kit.bytes.compare( bytes, 0, buffer.bytes, 0, buffer.size() ) == 0;
	}

	public boolean endsWith( Buffer buffer )
	{
		if( buffer.size() > bytes.length )
			return false;
		return Kit.bytes.compare( bytes, bytes.length - buffer.size(), buffer.bytes, 0, buffer.size() ) == 0;
	}

	@Override public int compareTo( Buffer other )
	{
		int commonLength = Math.min( bytes.length, other.bytes.length );
		int d = compareTo( other, commonLength );
		if( d != 0 )
			return d;
		return Integer.compare( bytes.length, other.bytes.length );
	}

	public int compareTo( Buffer other, int commonLength )
	{
		return Kit.bytes.compare( bytes, 0, other.bytes, 0, commonLength );
	}

	@Deprecated @Override public boolean equals( Object other )
	{
		return other instanceof Buffer kin && equals( kin );
	}

	public boolean equals( Buffer other )
	{
		if( hashCode() != other.hashCode() )
			return false;
		return Arrays.equals( bytes, other.bytes );
	}

	public boolean equals( byte... bytes )
	{
		return equals( of( bytes ) );
	}

	@SuppressWarnings( "NonFinalFieldReferencedInHashCode" ) @Override public int hashCode()
	{
		if( lazyHashCode == 0 )
			lazyHashCode = Arrays.hashCode( bytes );
		return lazyHashCode;
	}

	public byte[] getBytes()
	{
		if( bytes.length == 0 )
			return bytes;
		return Arrays.copyOf( bytes, bytes.length );
	}

	public byte[] getBytes( int offset, int length )
	{
		if( length == 0 )
			return arrayOfZeroBytes;
		return Arrays.copyOfRange( bytes, offset, offset + length );
	}

	public Buffer subset( int offset, int length )
	{
		if( length == 0 )
			return EMPTY;
		return of( Arrays.copyOfRange( bytes, offset, offset + length ) );
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

	public int indexOf( byte value )
	{
		return Kit.bytes.indexOf( bytes, 0, size(), value );
	}

	public int indexOf( byte value, int fromIndex )
	{
		return Kit.bytes.indexOf( bytes, fromIndex, size(), value );
	}

	public int lastIndexOf( byte value )
	{
		return Kit.bytes.lastIndexOf( bytes, 0, size(), value );
	}

	public int lastIndexOf( byte value, int fromIndex )
	{
		return Kit.bytes.lastIndexOf( bytes, fromIndex, size(), value );
	}

	public int indexOf( Buffer pattern )
	{
		return Kit.bytes.indexOf( bytes, 0, size(), pattern.bytes );
	}

	public int indexOf( Buffer pattern, int fromIndex )
	{
		return Kit.bytes.indexOf( bytes, fromIndex, size() - fromIndex, pattern.bytes );
	}

	public int lastIndexOf( Buffer pattern )
	{
		return Kit.bytes.lastIndexOf( bytes, size(), size(), pattern.bytes );
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

	public int caseInsensitiveHashCode()
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
		return size() == 0;
	}

	public Buffer subBuffer( int beginIndex )
	{
		return subBuffer( beginIndex, size() );
	}

	public Buffer subBuffer( int beginIndex, int endIndex )
	{
		assert beginIndex >= 0;
		assert endIndex <= size();
		if( beginIndex == 0 && endIndex == size() )
			return this;
		return of( bytes, beginIndex, endIndex - beginIndex );
	}

	/**
	 * Splits the {@link Buffer} on a given delimiter.
	 *
	 * @param delimiter the delimiter to split on.
	 * @param trim      whether to trim whitespace from the generated strings.
	 *
	 * @return an array of {@link Buffer} containing one {@link Buffer} for each part.
	 */
	public Buffer[] split( Buffer delimiter, boolean trim )
	{
		assert delimiter != null : new NullPointerException();
		assert !delimiter.isEmpty() : new IllegalArgumentException();
		Collection<Buffer> list = new ArrayList<>();
		boolean first = true;
		for( int position = 0; position < size(); )
		{
			if( first )
				first = false;
			else
			{
				assert subBuffer( position, position + delimiter.size() ).equals( delimiter );
				position += delimiter.size();
			}
			if( trim )
				position = skipWhitespaceForward( position, size() );
			int i = position;
			position = indexOf( delimiter, i );
			if( position == -1 )
				position = size();
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
	 * @return an array of {@link Buffer} containing one {@link Buffer} for each part.
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
	 * @return an array of {@link Buffer} containing one {@link Buffer} for each part.
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
	 * @return an array of {@link Buffer} containing one {@link Buffer} for each part.
	 */
	public Buffer[] split( byte delimiter )
	{
		Buffer delimiterAsBuffer = of( delimiter );
		return split( delimiterAsBuffer, false );
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

	private int skipWhitespaceForward( int start, int end )
	{
		while( start < end && Kit.bytes.isWhitespace( bytes[start] ) )
			start++;
		return start;
	}

	private int skipWhitespaceBackward( int start, int end )
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

	public void copyTo( int arrayOffset, byte[] destinationBytes )
	{
		System.arraycopy( bytes, arrayOffset, destinationBytes, 0, size() );
	}

	public String toHexString()
	{
		return Kit.bytes.hexString( bytes, " " );
	}
}
