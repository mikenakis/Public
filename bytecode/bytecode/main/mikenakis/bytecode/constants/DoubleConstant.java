package mikenakis.bytecode.constants;

import mikenakis.bytecode.Constant;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.kit.Buffer;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

/**
 * Represents the JVMS::CONSTANT_Double_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class DoubleConstant extends ValueConstant<Double>
{
	public static final int TAG = 6; // JVMS::CONSTANT_Double_info
	public static final ValueConstant.Kind<Double> KIND = new ValueConstant.Kind<>( TAG, "Double", Double.class )
	{
		@Override public Buffer readBuffer( BufferReader bufferReader )
		{
			return bufferReader.readBuffer( 8 );
		}

		@Override public Constant parse( ConstantPool constantPool, BufferReader bufferReader )
		{
			return new DoubleConstant( bufferReader );
		}
	};

	public final double value;

	public DoubleConstant( double value )
	{
		super( KIND );
		this.value = value;
	}

	private DoubleConstant( BufferReader bufferReader )
	{
		super( KIND );
		value = bufferReader.readDouble();
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		bufferWriter.writeDouble( value );
	}

	@Deprecated @Override public DoubleConstant asDoubleConstant()
	{
		return this;
	}

	@Override public boolean equals( Object other )
	{
		if( other instanceof DoubleConstant )
		{
			DoubleConstant otherDoubleConstant = (DoubleConstant)other;
			return Double.doubleToLongBits( value ) == Double.doubleToLongBits( otherDoubleConstant.value );
		}
		return false;
	}

	@Override public Double getValue()
	{
		return value;
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( value );
	}
}
