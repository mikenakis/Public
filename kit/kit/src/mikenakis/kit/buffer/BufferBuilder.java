package mikenakis.kit.buffer;

import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;
import mikenakis.kit.mutation.SingleThreadedMutationContext;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public final class BufferBuilder extends Mutable
{
	public static BufferBuilder of()
	{
		return of( SingleThreadedMutationContext.instance() );
	}

	public static BufferBuilder of( MutationContext mutationContext )
	{
		return of( mutationContext, 16 );
	}

	public static BufferBuilder of( MutationContext mutationContext, int capacity )
	{
		return new BufferBuilder( mutationContext, capacity );
	}

	private byte[] value;
	private int count;

	private BufferBuilder( MutationContext mutationContext, int capacity )
	{
		super( mutationContext );
		value = new byte[capacity];
		count = 0;
	}

	public int getLength()
	{
		assert inMutationContextAssertion();
		return count;
	}

	public int getCapacity()
	{
		assert inMutationContextAssertion();
		return value.length;
	}

	public void ensureCapacity( int minimumCapacity )
	{
		assert inMutationContextAssertion();
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
		assert inMutationContextAssertion();
		if( count < value.length )
			value = Arrays.copyOf( value, count );
	}

	public void setLength( int newLength )
	{
		assert inMutationContextAssertion();
		assert newLength >= 0 : new IndexOutOfBoundsException( Integer.toString( newLength ) );
		ensureCapacityInternal( newLength );
		if( count < newLength )
			Arrays.fill( value, count, newLength, (byte)0 );
		count = newLength;
	}

	public byte charAt( int index )
	{
		assert inMutationContextAssertion();
		return value[index];
	}

	public void getChars( int srcBegin, int srcEnd, byte[] dst, int dstBegin )
	{
		assert inMutationContextAssertion();
		System.arraycopy( value, srcBegin, dst, dstBegin, srcEnd - srcBegin );
	}

	public void setCharAt( int index, byte ch )
	{
		assert inMutationContextAssertion();
		value[index] = ch;
	}

	public BufferBuilder append( Object obj )
	{
		assert inMutationContextAssertion();
		return append( String.valueOf( obj ) );
	}

	public BufferBuilder append( String str )
	{
		assert inMutationContextAssertion();
		return append( str, StandardCharsets.UTF_8 );
	}

	public BufferBuilder append( String str, Charset charset )
	{
		assert inMutationContextAssertion();
		Buffer buffer = Buffer.of( str, charset );
		append( buffer );
		return this;
	}

	public BufferBuilder append( Buffer buffer )
	{
		assert inMutationContextAssertion();
		return append( buffer, 0, buffer.size() );
	}

	public BufferBuilder append( byte[] str )
	{
		assert inMutationContextAssertion();
		int len = str.length;
		ensureCapacityInternal( count + len );
		System.arraycopy( str, 0, value, count, len );
		count += len;
		return this;
	}

	public BufferBuilder append( char[] str )
	{
		assert inMutationContextAssertion();
		return append( str, StandardCharsets.UTF_8 );
	}

	public BufferBuilder append( char[] str, Charset charset )
	{
		assert inMutationContextAssertion();
		return append( Buffer.of( str, charset ) );
	}

	public BufferBuilder append( byte[] bytes, int offset, int len )
	{
		assert inMutationContextAssertion();
		if( len > 0 )
			ensureCapacityInternal( count + len );
		System.arraycopy( bytes, offset, value, count, len );
		count += len;
		return this;
	}

	public BufferBuilder append( Buffer buffer, int offset, int len )
	{
		assert inMutationContextAssertion();
		if( len > 0 )
			ensureCapacityInternal( count + len );
		buffer.copyBytes( offset, value, count, len );
		count += len;
		return this;
	}

	public BufferBuilder append( char[] str, int offset, int len )
	{
		assert inMutationContextAssertion();
		return append( Buffer.of( str ), offset, len );
	}

	public BufferBuilder append( boolean b )
	{
		assert inMutationContextAssertion();
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

	public BufferBuilder append( byte b )
	{
		assert inMutationContextAssertion();
		ensureCapacityInternal( count + 1 );
		value[count++] = b;
		return this;
	}

	public BufferBuilder append( char c )
	{
		assert inMutationContextAssertion();
		char[] chars = { c };
		return append( chars );
	}

	public BufferBuilder append( int i )
	{
		assert inMutationContextAssertion();
		String s = Integer.toString( i );
		append( s );
		return this;
	}

	public BufferBuilder append( long l )
	{
		assert inMutationContextAssertion();
		String s = Long.toString( l );
		append( s );
		return this;
	}

	public BufferBuilder append( float f )
	{
		assert inMutationContextAssertion();
		String s = Float.toString( f );
		append( s );
		return this;
	}

	public BufferBuilder append( double d )
	{
		assert inMutationContextAssertion();
		String s = Double.toString( d );
		append( s );
		return this;
	}

	public BufferBuilder delete( int start, int end )
	{
		assert inMutationContextAssertion();
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
		assert inMutationContextAssertion();
		if( (index < 0) || (index >= count) )
			throw new StringIndexOutOfBoundsException( index );
		System.arraycopy( value, index + 1, value, index, count - index - 1 );
		count--;
		return this;
	}

	public BufferBuilder replace( int start, int end, String str )
	{
		assert inMutationContextAssertion();
		return replace( start, end, Buffer.of( str ) );
	}

	public BufferBuilder replace( int start, int end, Buffer buffer )
	{
		assert inMutationContextAssertion();
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
		assert inMutationContextAssertion();
		return substring( start, count );
	}

	public String substring( int start, int end )
	{
		assert inMutationContextAssertion();
		return new String( value, start, end - start );
	}

	public BufferBuilder insert( int index, byte[] str, int offset, int len )
	{
		assert inMutationContextAssertion();
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
		assert inMutationContextAssertion();
		return insert( offset, Buffer.valueOf( obj ) );
	}

	public BufferBuilder insert( int offset, byte[] str )
	{
		assert inMutationContextAssertion();
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
		assert inMutationContextAssertion();
		if( buffer == null )
			buffer = NULL;
		return insert( dstOffset, buffer, 0, buffer.size() );
	}

	public BufferBuilder insert( int dstOffset, Buffer buffer, int start, int end )
	{
		assert inMutationContextAssertion();
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
		assert inMutationContextAssertion();
		return insert( offset, String.valueOf( b ) );
	}

	public BufferBuilder insert( int offset, byte b )
	{
		assert inMutationContextAssertion();
		ensureCapacityInternal( count + 1 );
		System.arraycopy( value, offset, value, offset + 1, count - offset );
		value[offset] = b;
		count += 1;
		return this;
	}

	public BufferBuilder insert( int offset, int i )
	{
		assert inMutationContextAssertion();
		return insert( offset, String.valueOf( i ) );
	}

	public BufferBuilder insert( int offset, long l )
	{
		assert inMutationContextAssertion();
		return insert( offset, String.valueOf( l ) );
	}

	public BufferBuilder insert( int offset, float f )
	{
		assert inMutationContextAssertion();
		return insert( offset, String.valueOf( f ) );
	}

	public BufferBuilder insert( int offset, double d )
	{
		assert inMutationContextAssertion();
		return insert( offset, String.valueOf( d ) );
	}

	public int indexOf( Buffer pattern )
	{
		assert inMutationContextAssertion();
		return Buffer.indexOf( value, pattern );
	}

	public int lastIndexOf( Buffer pattern )
	{
		assert inMutationContextAssertion();
		return Buffer.lastIndexOf( value, pattern );
	}

	public BufferBuilder reverse()
	{
		assert inMutationContextAssertion();
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
		assert inMutationContextAssertion();
		return new String( value, 0, count );
	}

	public Buffer toBuffer()
	{
		assert inMutationContextAssertion();
		return Buffer.of( value, 0, count );
	}
}
