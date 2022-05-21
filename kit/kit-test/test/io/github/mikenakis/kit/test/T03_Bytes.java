package io.github.mikenakis.kit.test;

import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.kit.functional.Function1;
import io.github.mikenakis.kit.functional.Procedure1;
import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * Test.
 */
public class T03_Bytes
{
	public T03_Bytes()
	{
		if( !Kit.areAssertionsEnabled() )
			throw new AssertionError();
	}

	@Test
	public void Short_Works()
	{
		for( int i = 0; i < Short.SIZE; i++ )
		{
			short value = (short)(1 << i);
			testShort( value );
		}
	}

	private static void testShort( short value )
	{
		byte[] bytes1 = Kit.bytes.bytesFromShort( value );
		byte[] bytes2 = jvmBytesFromShort( value );
		assert Kit.bytes.compare( bytes1, bytes2 ) == 0;
		short result = Kit.bytes.shortFromBytes( bytes1 );
		assert result == jvmShortFromBytes( bytes1 );
		assert result == value;
	}

	@Test
	public void Int_Works()
	{
		for( int i = 0; i < Integer.SIZE; i++ )
		{
			int value = 1 << i;
			testInt( value );
		}
	}

	private static void testInt( int value )
	{
		byte[] bytes1 = Kit.bytes.bytesFromInt( value );
		byte[] bytes2 = jvmBytesFromInt( value );
		assert Kit.bytes.compare( bytes1, bytes2 ) == 0;
		int result = Kit.bytes.intFromBytes( bytes1 );
		assert result == value;
		assert result == jvmIntFromBytes( bytes1 );
	}

	@Test
	public void Long_Works()
	{
		for( int i = 0; i < Long.SIZE; i++ )
		{
			long value = 1L << i;
			byte[] bytes1 = Kit.bytes.bytesFromLong( value );
			byte[] bytes2 = jvmBytesFromLong( value );
			assert Kit.bytes.compare( bytes1, bytes2 ) == 0;
			long result = Kit.bytes.longFromBytes( bytes1 );
			assert result == value;
			assert result == jvmLongFromBytes( bytes1 );
		}
	}

	private static byte[] jvmBytesFromShort( short x )
	{
		return jvmBytesFromValue( Short.BYTES, byteBuffer -> byteBuffer.putShort( x ) );
	}

	private static short jvmShortFromBytes( byte[] bytes )
	{
		return jvmValueFromBytes( Short.BYTES, bytes, byteBuffer -> byteBuffer.getShort() );
	}

	private static byte[] jvmBytesFromInt( int x )
	{
		return jvmBytesFromValue( Integer.BYTES, byteBuffer -> byteBuffer.putInt( x ) );
	}

	private static int jvmIntFromBytes( byte[] bytes )
	{
		return jvmValueFromBytes( Integer.BYTES, bytes, byteBuffer -> byteBuffer.getInt() );
	}

	private static byte[] jvmBytesFromLong( long x )
	{
		return jvmBytesFromValue( Long.BYTES, byteBuffer -> byteBuffer.putLong( x ) );
	}

	private static long jvmLongFromBytes( byte[] bytes )
	{
		return jvmValueFromBytes( Long.BYTES, bytes, byteBuffer -> byteBuffer.getLong() );
	}

	private static byte[] jvmBytesFromValue( int size, Procedure1<ByteBuffer> setter )
	{
		ByteBuffer buffer = ByteBuffer.allocate( size );
		setter.invoke( buffer );
		return buffer.array();
	}

	private static <T> T jvmValueFromBytes( int size, byte[] bytes, Function1<T,ByteBuffer> getter )
	{
		ByteBuffer buffer = ByteBuffer.allocate( size );
		buffer.put( bytes );
		buffer.flip();//need flip
		return getter.invoke( buffer );
	}
}
