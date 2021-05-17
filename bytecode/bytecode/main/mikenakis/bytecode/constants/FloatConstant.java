package mikenakis.bytecode.constants;

import mikenakis.bytecode.Constant;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.kit.Buffer;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

/**
 * Represents the JVMS::CONSTANT_Float_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class FloatConstant extends ValueConstant<Float>
{
	public static final int TAG = 4; // JVMS::CONSTANT_Float_info
	public static final ValueConstant.Kind<Float> KIND = new ValueConstant.Kind<>( TAG, "Float", Float.class )
	{
		@Override public Buffer readBuffer( BufferReader bufferReader )
		{
			return bufferReader.readBuffer( 4 );
		}

		@Override public Constant parse( ConstantPool constantPool, BufferReader bufferReader )
		{
			return new FloatConstant( bufferReader );
		}
	};

	public final float value;

	public FloatConstant( float value )
	{
		super( KIND );
		this.value = value;
	}

	private FloatConstant( BufferReader bufferReader )
	{
		super( KIND );
		value = bufferReader.readFloat();
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		bufferWriter.writeFloat( value );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( value ).append( 'f' );
	}

	@Deprecated @Override public FloatConstant asFloatConstant()
	{
		return this;
	}

	@Override public boolean equals( Object other )
	{
		if( other instanceof FloatConstant )
		{
			FloatConstant otherFloatConstant = (FloatConstant)other;
			return Float.floatToIntBits( value ) == Float.floatToIntBits( otherFloatConstant.value );
		}
		return false;
	}

	@Override public Float getValue()
	{
		return value;
	}
}
