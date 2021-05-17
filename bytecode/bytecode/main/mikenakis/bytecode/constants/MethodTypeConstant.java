package mikenakis.bytecode.constants;

import mikenakis.bytecode.Constant;
import mikenakis.bytecode.ConstantKind;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.kit.Buffer;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

import java.util.Objects;

/**
 * Represents the JVMS::CONSTANT_MethodType_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class MethodTypeConstant extends Constant
{
	public static final int TAG = 16; // JVMS::CONSTANT_MethodType_info
	public static final ConstantKind KIND = new ConstantKind( TAG, "MethodType" )
	{
		@Override public Buffer readBuffer( BufferReader bufferReader )
		{
			return bufferReader.readBuffer( 2 );
		}

		@Override public Constant parse( ConstantPool constantPool, BufferReader bufferReader )
		{
			return new MethodTypeConstant( constantPool, bufferReader );
		}
	};

	public final Utf8Constant descriptorConstant;

	public MethodTypeConstant( String descriptor )
	{
		super( KIND );
		descriptorConstant = new Utf8Constant( descriptor );
	}

	private MethodTypeConstant( ConstantPool constantPool, BufferReader bufferReader )
	{
		super( KIND );
		descriptorConstant = constantPool.readIndexAndGetConstant( bufferReader ).asUtf8Constant();
	}

	@Override public void intern( ConstantPool constantPool )
	{
		descriptorConstant.intern( constantPool );
		super.intern( constantPool );
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		descriptorConstant.writeIndex( constantPool, bufferWriter );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( "descriptor = " );
		descriptorConstant.toStringBuilder( builder );
	}

	@Deprecated @Override public MethodTypeConstant asMethodTypeConstant()
	{
		return this;
	}

	@Override public boolean equals( Object other )
	{
		if( other instanceof MethodTypeConstant )
		{
			MethodTypeConstant otherMethodTypeConstant = (MethodTypeConstant)other;
			return descriptorConstant.equalsUtf8Constant( otherMethodTypeConstant.descriptorConstant );
		}
		return false;
	}

	@Override public int hashCode()
	{
		return Objects.hash( kind, descriptorConstant );
	}
}
