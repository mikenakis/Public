package io.github.mikenakis.bytecode.model.constants.value;

import io.github.mikenakis.bytecode.exceptions.IncompleteMutf8Exception;
import io.github.mikenakis.bytecode.exceptions.MalformedMutf8Exception;
import io.github.mikenakis.bytecode.exceptions.StringTooLongException;
import io.github.mikenakis.bytecode.kit.Buffer;
import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.kit.BufferWriter;
import io.github.mikenakis.bytecode.model.constants.ValueConstant;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.bytecode.writing.WritingBootstrapPool;
import io.github.mikenakis.bytecode.writing.WritingConstantPool;
import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Objects;

/**
 * Represents the JVMS::CONSTANT_Utf8_info structure.
 * <p>
 * Note: "MUTF8" stands for "Modified UTF8"
 *
 * @author michael.gr
 */
public final class Mutf8ValueConstant extends ValueConstant
{
	public static Mutf8ValueConstant read( BufferReader bufferReader, int constantTag )
	{
		assert constantTag == tag_Mutf8;
		int length = bufferReader.readUnsignedShort();
		Buffer buffer = bufferReader.readBuffer( length );
		return of( buffer );
	}

	public static Mutf8ValueConstant of( String stringValue )
	{
		return new Mutf8ValueConstant( null, stringValue );
	}

	public static Mutf8ValueConstant of( Buffer buffer )
	{
		return new Mutf8ValueConstant( buffer, null );
	}

	@SuppressWarnings( "FieldNamingConvention" ) private Buffer _buffer;
	@SuppressWarnings( "FieldNamingConvention" ) private String _stringValue;

	private Mutf8ValueConstant( Buffer buffer, String stringValue )
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

	@Deprecated @Override public Mutf8ValueConstant asMutf8ValueConstant() { return this; }

	@Deprecated @Override public boolean equals( Object other ) { return other instanceof Mutf8ValueConstant kin && equals( kin ); }

	public boolean equals( Mutf8ValueConstant other )
	{
		if( _stringValue != null && other._stringValue != null )
			return stringValue().equals( other.stringValue() );
		return buffer().equals( other.buffer() );
	}

	private static String stringFromBuffer( Buffer buffer )
	{
		String result = stringFromBuffer0( buffer );
		assert bufferFromString0( result ).equals( buffer );
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
		assert stringFromBuffer( result ).equals( s );
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

	@Override public int hashCode() { return Objects.hash( tag, buffer() ); }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return Kit.string.escapeForJava( stringValue() ); }

	@Override public void intern( Interner interner )
	{
		interner.intern( this );
	}

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, WritingBootstrapPool bootstrapPool )
	{
		bufferWriter.writeUnsignedByte( tag );
		Buffer buffer = buffer();
		bufferWriter.writeUnsignedShort( buffer.length() );
		bufferWriter.writeBuffer( buffer );
	}
}
