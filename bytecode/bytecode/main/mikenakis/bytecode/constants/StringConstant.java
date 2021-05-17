package mikenakis.bytecode.constants;

import mikenakis.bytecode.Constant;
import mikenakis.bytecode.ConstantKind;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.kit.Buffer;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

import java.util.Objects;

/**
 * Represents the JVMS::CONSTANT_String_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class StringConstant extends Constant
{
	public static final int TAG = 8; // JVMS::CONSTANT_String_info
	public static final ConstantKind KIND = new ConstantKind( TAG, "String" )
	{
		@Override public Buffer readBuffer( BufferReader bufferReader )
		{
			return bufferReader.readBuffer( 2 );
		}

		@Override public Constant parse( ConstantPool constantPool, BufferReader bufferReader )
		{
			return new StringConstant( constantPool, bufferReader );
		}
	};

	public final Utf8Constant valueUtf8Constant;

	public StringConstant( String value )
	{
		super( KIND );
		valueUtf8Constant = new Utf8Constant( value );
	}

	public StringConstant( Utf8Constant valueUtf8Constant )
	{
		super( KIND );
		assert valueUtf8Constant != null;
		this.valueUtf8Constant = valueUtf8Constant;
	}

	private StringConstant( ConstantPool constantPool, BufferReader bufferReader )
	{
		super( KIND );
		int valueIndex = bufferReader.readUnsignedShort();
		valueUtf8Constant = constantPool.getConstant( valueIndex ).asUtf8Constant();
	}

	@Override public void intern( ConstantPool constantPool )
	{
		valueUtf8Constant.intern( constantPool );
		super.intern( constantPool );
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		valueUtf8Constant.writeIndex( constantPool, bufferWriter );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( "utf8 = " );
		valueUtf8Constant.toStringBuilder( builder );
	}

	@Deprecated @Override public StringConstant asStringConstant()
	{
		return this;
	}

	@Override public boolean equals( Object other )
	{
		if( other instanceof StringConstant )
		{
			StringConstant stringConstant = (StringConstant)other;
			return valueUtf8Constant.equals( stringConstant.valueUtf8Constant );
		}
		return false;
	}

	@Override public int hashCode()
	{
		return Objects.hash( kind, valueUtf8Constant );
	}
}
