package io.github.mikenakis.bytecode.kit;

public final class Helpers
{
	public static boolean isSignedByte( int value )
	{
		return value >= -128 && value <= 127;
	}

	public static boolean isSignedShort( int value )
	{
		return value >= -32768 && value <= 32767;
	}

	public static boolean isUnsignedByte( int value )
	{
		return (value & ~0xff) == 0;
	}

	public static boolean isUnsignedShort( int value )
	{
		return (value & ~0xffff) == 0;
	}

	public static int signExtendByte( int value )
	{
		assert isUnsignedByte( value );
		int result = value << 24 >> 24;
		assert isSignedByte( result );
		return result;
	}

	public static int signExtendShort( int value )
	{
		assert isUnsignedShort( value );
		int result = value << 16 >> 16;
		assert isSignedShort( result );
		return result;
	}

	public static int padding( int position )
	{
		int result = (((position & 1) << 1) ^ position) & 3;
		assert result >= 0 && result <= 3;
		assert ((position + result) & 3) == 0;
		return result;
	}
}
