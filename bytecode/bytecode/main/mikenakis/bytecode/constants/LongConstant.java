package mikenakis.bytecode.constants;

import mikenakis.bytecode.Constant;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.kit.Buffer;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

/**
 * Represents the JVMS::CONSTANT_Long_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class LongConstant extends ValueConstant<Long>
{
	public static final int TAG = 5; // JVMS::CONSTANT_Long_info
	public static final ValueConstant.Kind<Long> KIND = new ValueConstant.Kind<>( TAG, "Long", Long.class )
	{
		@Override public Buffer readBuffer( BufferReader bufferReader )
		{
			return bufferReader.readBuffer( 8 );
		}

		@Override public Constant parse( ConstantPool constantPool, BufferReader bufferReader )
		{
			return new LongConstant( bufferReader );
		}
	};

	public final long value;

	public LongConstant( long value )
	{
		super( KIND );
		this.value = value;
	}

	private LongConstant( BufferReader bufferReader )
	{
		super( KIND );
		value = bufferReader.readLong();
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		bufferWriter.writeLong( value );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( value ).append( 'L' );
	}

	@Deprecated @Override public LongConstant asLongConstant()
	{
		return this;
	}

	@Override public boolean equals( Object other )
	{
		if( other instanceof LongConstant )
		{
			LongConstant otherLongConstant = (LongConstant)other;
			return value == otherLongConstant.value;
		}
		return false;
	}

	@Override public Long getValue()
	{
		return value;
	}
}
