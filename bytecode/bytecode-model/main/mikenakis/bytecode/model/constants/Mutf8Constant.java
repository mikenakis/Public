package mikenakis.bytecode.model.constants;

import mikenakis.bytecode.exceptions.IncompleteMutf8Exception;
import mikenakis.bytecode.exceptions.MalformedMutf8Exception;
import mikenakis.bytecode.exceptions.StringTooLongException;
import mikenakis.bytecode.kit.Buffer;
import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Objects;

/**
 * Represents the JVMS::CONSTANT_Utf8_info structure.
 *
 * Note: "MUTF8" stands for "Modified UTF8"
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class Mutf8Constant extends ValueConstant<String>
{
	public static Mutf8Constant of( String stringValue )
	{
		return new Mutf8Constant( null, stringValue );
	}

	public static Mutf8Constant of( Buffer buffer )
	{
		return new Mutf8Constant( buffer, null );
	}

	@SuppressWarnings( "FieldNamingConvention" ) private Buffer _buffer;
	@SuppressWarnings( "FieldNamingConvention" ) private String _stringValue;

	private Mutf8Constant( Buffer buffer, String stringValue )
	{
		super( tag_Mutf8 );
		assert (stringValue == null) != (buffer == null);
		_buffer = buffer;
		_stringValue = stringValue;
	}

	public Buffer buffer()
	{
		if( _buffer == null )
			_buffer = bufferFromString( _stringValue );
		return _buffer;
	}

	public String stringValue()
	{
		if( _stringValue == null )
			_stringValue = stringFromBuffer( _buffer );
		return _stringValue;
	}

	@Deprecated @Override public String value()
	{
		return stringValue();
	}

	@Deprecated @Override public Mutf8Constant asMutf8Constant()
	{
		return this;
	}

	@Deprecated @Override public boolean equals( Object other )
	{
		if( other instanceof Mutf8Constant otherMutf8Constant )
			return equalsMutf8Constant( otherMutf8Constant );
		return false;
	}

	public boolean equalsMutf8Constant( Mutf8Constant other )
	{
		if( _stringValue != null && other._stringValue != null )
			return stringValue().equals( other.stringValue() );
		return buffer().equalsBuffer( other.buffer() );
	}

	private static String stringFromBuffer( Buffer buffer )
	{
		String result = stringFromBuffer0( buffer );
		//assert bufferFromString0( result ).equals( buffer );
		return result;
	}

	private static String stringFromBuffer0( Buffer buffer )
	{
		byte[] bytes = buffer.getBytes();
		int count = bytes.length;
		char[] chars = new char[count];
		int t = 0;

		int s;
		for( s = 0; s < count; s++ )
		{
			int c = bytes[s] & 0xff;
			if( c > 127 )
				break;
			chars[t++] = (char)c;
		}

		while( s < count )
		{
			int b1 = bytes[s++] & 0xff;
			assert b1 >> 4 >= 0; //this is guaranteed, since 0 <= b1 <= 255
			if( b1 >> 4 <= 7 ) /* 0x0xxx_xxxx */
				chars[t++] = (char)b1;
			else if( b1 >> 4 <= 11 ) /* 0x10xx_xxxx */
				throw new MalformedMutf8Exception( s - 1 );
			else if( b1 >> 4 <= 13 ) /* 0x110x_xxxx 0x10xx_xxxx */
			{
				assert s < count : new IncompleteMutf8Exception( s - 1 );
				int b2 = bytes[s++] & 0xff;
				assert (b2 & 0xc0) == 0x80 : new MalformedMutf8Exception( s - 1 );
				chars[t++] = (char)(((b1 & 0x1f) << 6) | (b2 & 0x3f));
			}
			else if( b1 >> 4 == 14 ) /* 0x1110_xxxx 0x10xx_xxxx 0x10xx_xxxx */
			{
				assert s < count : new IncompleteMutf8Exception( s - 1 );
				int b2 = bytes[s++] & 0xff;
				assert (b2 & 0xc0) == 0x80 : new MalformedMutf8Exception( s - 1 );
				assert s < count : new IncompleteMutf8Exception( s - 1 );
				int b3 = bytes[s++] & 0xff;
				assert (b3 & 0xc0) == 0x80 : new MalformedMutf8Exception( s - 1 );
				chars[t++] = (char)(((b1 & 0x0f) << 12) | ((b2 & 0x3f) << 6) | (b3 & 0x3f));
			}
			else /* 0x1111_xxxx */
				throw new MalformedMutf8Exception( s - 1 );
		}
		return new String( chars, 0, t );
	}

	private static Buffer bufferFromString( String s )
	{
		Buffer result = bufferFromString0( s );
		//assert stringFromBuffer( result ).equals( s );
		return result;
	}

	private static Buffer bufferFromString0( String s )
	{
		char[] chars = s.toCharArray();
		byte[] bytes = new byte[chars.length * 3];
		int p = 0;
		for( char c : chars )
		{
			if( (c >= 1) && (c <= 0x7f) )
				bytes[p++] = (byte)c;
			else if( c > 0x07ff )
			{
				bytes[p++] = (byte)(0xe0 | ((c >> 12) & 0x0f));
				bytes[p++] = (byte)(0x80 | ((c >> 6) & 0x3f));
				bytes[p++] = (byte)(0x80 | (c & 0x3f));
			}
			else
			{
				bytes[p++] = (byte)(0xc0 | ((c >> 6) & 0x1f));
				bytes[p++] = (byte)(0x80 | (c & 0x3f));
			}
		}
		assert p < 0xffff : new StringTooLongException( p );
		return Buffer.of( bytes, 0, p );
	}

	@Override public int hashCode()
	{
		return Objects.hash( tag, buffer() );
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return Kit.string.escapeForJava( stringValue() );
	}
}