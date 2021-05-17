package mikenakis.bytecode.constants;

import mikenakis.bytecode.Constant;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.kit.Buffer;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

/**
 * Represents the JVMS::CONSTANT_Integer_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class IntegerConstant extends ValueConstant<Integer>
{
	public static final int TAG = 3; // JVMS::CONSTANT_Integer_info
	public static final ValueConstant.Kind<Integer> KIND = new ValueConstant.Kind<>( TAG, "Integer", Integer.class )
	{
		@Override public Buffer readBuffer( BufferReader bufferReader )
		{
			return bufferReader.readBuffer( 4 );
		}

		@Override public Constant parse( ConstantPool constantPool, BufferReader bufferReader )
		{
			return new IntegerConstant( bufferReader );
		}
	};

	public final int value;

	public IntegerConstant( int value )
	{
		super( KIND );
		this.value = value;
	}

	private IntegerConstant( BufferReader bufferReader )
	{
		super( KIND );
		value = bufferReader.readInt();
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		bufferWriter.writeInt( value );
	}

	@Deprecated @Override public IntegerConstant asIntegerConstant()
	{
		return this;
	}

	@Override public boolean equals( Object other )
	{
		if( other instanceof IntegerConstant )
		{
			IntegerConstant otherIntegerConstant = (IntegerConstant)other;
			return value == otherIntegerConstant.value;
		}
		return false;
	}

	@Override public Integer getValue()
	{
		return value;
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( value );
	}
}
