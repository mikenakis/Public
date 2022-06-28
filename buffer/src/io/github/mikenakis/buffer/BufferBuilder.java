package io.github.mikenakis.buffer;

import io.github.mikenakis.coherence.AbstractCoherent;
import io.github.mikenakis.coherence.Coherence;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public final class BufferBuilder extends AbstractCoherent
{
	public static BufferBuilder of( Coherence coherence )
	{
		return of( coherence, 16 );
	}

	public static BufferBuilder of( Coherence coherence, int capacity )
	{
		return new BufferBuilder( coherence, capacity );
	}

	private byte[] value;
	private int count;

	private BufferBuilder( Coherence coherence, int capacity )
	{
		super( coherence );
		value = new byte[capacity];
		count = 0;
	}

	public int getLength()
	{
		assert mustBeReadableAssertion();
		return count;
	}

	public int getCapacity()
	{
		assert mustBeReadableAssertion();
		return value.length;
	}

	public void ensureCapacity( int minimumCapacity )
	{
		assert mustBeWritableAssertion();
		if( minimumCapacity > 0 )
			ensureCapacityInternal( minimumCapacity );
	}

	private void ensureCapacityInternal( int minimumCapacity )
	{
		// overflow-conscious code
		if( minimumCapacity - value.length > 0 )
			expandCapacity( minimumCapacity );
	}

	private void expandCapacity( int minimumCapacity )
	{
		int newCapacity = value.length * 2 + 2;
		if( newCapacity - minimumCapacity < 0 )
			newCapacity = minimumCapacity;
		if( newCapacity < 0 )
		{
			if( minimumCapacity < 0 ) // overflow
				throw new OutOfMemoryError();
			newCapacity = Integer.MAX_VALUE;
		}
		value = Arrays.copyOf( value, newCapacity );
	}

	public void trimToSize()
	{
		assert mustBeWritableAssertion();
		if( count < value.length )
			value = Arrays.copyOf( value, count );
	}

	public void setLength( int newLength )
	{
		assert mustBeWritableAssertion();
		assert newLength >= 0 : new IndexOutOfBoundsException( Integer.toString( newLength ) );
		ensureCapacityInternal( newLength );
		if( count < newLength )
			Arrays.fill( value, count, newLength, (byte)0 );
		count = newLength;
	}

	public byte charAt( int index )
	{
		assert mustBeReadableAssertion();
		return value[index];
	}

	public void getChars( int srcBegin, int srcEnd, byte[] dst, int dstBegin )
	{
		assert mustBeReadableAssertion();
		System.arraycopy( value, srcBegin, dst, dstBegin, srcEnd - srcBegin );
	}

	public void setCharAt( int index, byte ch )
	{
		assert mustBeWritableAssertion();
		value[index] = ch;
	}

	public BufferBuilder appendObjectText( Object obj )
	{
		assert mustBeWritableAssertion();
		return appendStringText( String.valueOf( obj ) );
	}

	public BufferBuilder appendStringText( String str )
	{
		assert mustBeWritableAssertion();
		return appendStringText( str, StandardCharsets.UTF_8 );
	}

	public BufferBuilder appendStringText( String str, Charset charset )
	{
		assert mustBeWritableAssertion();
		Buffer buffer = Buffer.of( str, charset );
		appendBuffer( buffer );
		return this;
	}

	public BufferBuilder appendBuffer( Buffer buffer )
	{
		assert mustBeWritableAssertion();
		return appendBytes( buffer, 0, buffer.size() );
	}

	public BufferBuilder appendBytes( byte[] bytes )
	{
		assert mustBeWritableAssertion();
		int len = bytes.length;
		ensureCapacityInternal( count + len );
		System.arraycopy( bytes, 0, value, count, len );
		count += len;
		return this;
	}

	public BufferBuilder appendText( char[] str )
	{
		assert mustBeWritableAssertion();
		return appendText( str, StandardCharsets.UTF_8 );
	}

	public BufferBuilder appendText( char[] str, Charset charset )
	{
		assert mustBeWritableAssertion();
		return appendBuffer( Buffer.of( str, charset ) );
	}

	public BufferBuilder appendText( byte[] bytes, int offset, int len )
	{
		assert mustBeWritableAssertion();
		if( len > 0 )
			ensureCapacityInternal( count + len );
		System.arraycopy( bytes, offset, value, count, len );
		count += len;
		return this;
	}

	public BufferBuilder appendBytes( Buffer buffer, int offset, int len )
	{
		assert mustBeWritableAssertion();
		if( len > 0 )
			ensureCapacityInternal( count + len );
		buffer.copyBytes( offset, value, count, len );
		count += len;
		return this;
	}

	public BufferBuilder appendText( char[] str, int offset, int len )
	{
		assert mustBeWritableAssertion();
		return appendBytes( Buffer.of( str ), offset, len );
	}

	public BufferBuilder appendBooleanText( boolean b )
	{
		assert mustBeWritableAssertion();
		//noinspection IfStatementWithIdenticalBranches
		if( b )
		{
			ensureCapacityInternal( count + 4 );
			value[count++] = 't';
			value[count++] = 'r';
			value[count++] = 'u';
			value[count++] = 'e';
		}
		else
		{
			ensureCapacityInternal( count + 5 );
			value[count++] = 'f';
			value[count++] = 'a';
			value[count++] = 'l';
			value[count++] = 's';
			value[count++] = 'e';
		}
		return this;
	}

	public BufferBuilder appendByte( byte b )
	{
		assert mustBeWritableAssertion();
		ensureCapacityInternal( count + 1 );
		value[count++] = b;
		return this;
	}

	public BufferBuilder appendCharText( char c )
	{
		assert mustBeWritableAssertion();
		char[] chars = { c };
		return appendText( chars );
	}

	public BufferBuilder appendIntText( int i )
	{
		assert mustBeWritableAssertion();
		String s = Integer.toString( i );
		appendStringText( s );
		return this;
	}

	public BufferBuilder appendLongText( long l )
	{
		assert mustBeWritableAssertion();
		String s = Long.toString( l );
		appendStringText( s );
		return this;
	}

	public BufferBuilder appendFloatText( float f )
	{
		assert mustBeWritableAssertion();
		String s = Float.toString( f );
		appendStringText( s );
		return this;
	}

	public BufferBuilder appendDoubleText( double d )
	{
		assert mustBeWritableAssertion();
		String s = Double.toString( d );
		appendStringText( s );
		return this;
	}

	public BufferBuilder delete( int start, int end )
	{
		assert mustBeWritableAssertion();
		if( start < 0 )
			throw new StringIndexOutOfBoundsException( start );
		if( end > count )
			end = count;
		if( start > end )
			throw new StringIndexOutOfBoundsException();
		int len = end - start;
		if( len > 0 )
		{
			System.arraycopy( value, start + len, value, start, count - end );
			count -= len;
		}
		return this;
	}

	public BufferBuilder deleteCharAt( int index )
	{
		assert mustBeWritableAssertion();
		if( (index < 0) || (index >= count) )
			throw new StringIndexOutOfBoundsException( index );
		System.arraycopy( value, index + 1, value, index, count - index - 1 );
		count--;
		return this;
	}

	public BufferBuilder replace( int start, int end, String str )
	{
		assert mustBeWritableAssertion();
		return replace( start, end, Buffer.of( str ) );
	}

	public BufferBuilder replace( int start, int end, Buffer buffer )
	{
		assert mustBeWritableAssertion();
		assert start >= 0 : new IndexOutOfBoundsException( Integer.toString( start ) );
		assert start <= count : new IndexOutOfBoundsException( start + " count: " + count );
		assert start <= end : new IndexOutOfBoundsException( start + " end: " + end );

		if( end > count )
			end = count;
		int len = buffer.size();
		int newCount = count + len - (end - start);
		ensureCapacityInternal( newCount );

		System.arraycopy( value, end, value, start + len, count - end );
		buffer.copyBytes( value, start );
		count = newCount;
		return this;
	}

	public String substring( int start )
	{
		assert mustBeReadableAssertion();
		return substring( start, count );
	}

	public String substring( int start, int end )
	{
		assert mustBeReadableAssertion();
		return new String( value, start, end - start );
	}

	public BufferBuilder insert( int index, byte[] str, int offset, int len )
	{
		assert mustBeWritableAssertion();
		assert index >= 0 : new IndexOutOfBoundsException( Integer.toString( index ) );
		assert index <= count : new IndexOutOfBoundsException( index + " count: " + count );
		assert offset >= 0 : new IndexOutOfBoundsException( Integer.toString( offset ) );
		assert len >= 0 : new IndexOutOfBoundsException( Integer.toString( len ) );
		assert offset <= str.length - len : new IndexOutOfBoundsException( "offset " + offset + ", len " + len + ", str.length " + str.length );
		ensureCapacityInternal( count + len );
		System.arraycopy( value, index, value, index + len, count - index );
		System.arraycopy( str, offset, value, index, len );
		count += len;
		return this;
	}

	public BufferBuilder insert( int offset, Object obj )
	{
		assert mustBeWritableAssertion();
		return insert( offset, Buffer.valueOf( obj ) );
	}

	public BufferBuilder insert( int offset, byte[] str )
	{
		assert mustBeWritableAssertion();
		assert offset >= 0;
		assert offset <= count;
		int len = str.length;
		ensureCapacityInternal( count + len );
		System.arraycopy( value, offset, value, offset + len, count - offset );
		System.arraycopy( str, 0, value, offset, len );
		count += len;
		return this;
	}

	private static final Buffer NULL = Buffer.of( "null" );

	public BufferBuilder insert( int dstOffset, Buffer buffer )
	{
		assert mustBeWritableAssertion();
		if( buffer == null )
			buffer = NULL;
		return insert( dstOffset, buffer, 0, buffer.size() );
	}

	public BufferBuilder insert( int dstOffset, Buffer buffer, int start, int end )
	{
		assert mustBeWritableAssertion();
		if( buffer == null )
			buffer = NULL;
		assert dstOffset >= 0;
		assert dstOffset <= count;
		assert start >= 0;
		assert end >= 0;
		assert start <= end;
		assert end <= buffer.size();
		int len = end - start;
		ensureCapacityInternal( count + len );
		System.arraycopy( value, dstOffset, value, dstOffset + len, count - dstOffset );
		buffer.copyBytes( start, value, dstOffset, len );
		count += len;
		return this;
	}

	public BufferBuilder insert( int offset, boolean b )
	{
		assert mustBeWritableAssertion();
		return insert( offset, String.valueOf( b ) );
	}

	public BufferBuilder insert( int offset, byte b )
	{
		assert mustBeWritableAssertion();
		ensureCapacityInternal( count + 1 );
		System.arraycopy( value, offset, value, offset + 1, count - offset );
		value[offset] = b;
		count += 1;
		return this;
	}

	public BufferBuilder insert( int offset, int i )
	{
		return insert( offset, String.valueOf( i ) );
	}

	public BufferBuilder insert( int offset, long l )
	{
		return insert( offset, String.valueOf( l ) );
	}

	public BufferBuilder insert( int offset, float f )
	{
		return insert( offset, String.valueOf( f ) );
	}

	public BufferBuilder insert( int offset, double d )
	{
		return insert( offset, String.valueOf( d ) );
	}

	public int indexOf( Buffer pattern )
	{
		assert mustBeReadableAssertion();
		return Buffer.indexOf( value, pattern );
	}

	public int lastIndexOf( Buffer pattern )
	{
		assert mustBeReadableAssertion();
		return Buffer.lastIndexOf( value, pattern );
	}

	public BufferBuilder reverse()
	{
		assert mustBeWritableAssertion();
		int n = count - 1;
		for( int j = (n - 1) >> 1; j >= 0; j-- )
		{
			int k = n - j;
			byte cj = value[j];
			byte ck = value[k];
			value[j] = ck;
			value[k] = cj;
		}
		return this;
	}

	@Override public String toString()
	{
		return new String( value, 0, count );
	}

	public Buffer toBuffer()
	{
		assert mustBeReadableAssertion();
		return Buffer.of( value, 0, count );
	}
}
