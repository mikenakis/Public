package mikenakis.bytecode.constants;

import mikenakis.bytecode.Constant;
import mikenakis.bytecode.ConstantKind;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.Descriptor;
import mikenakis.bytecode.kit.Buffer;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

import java.util.Objects;

/**
 * Represents the JVMS::CONSTANT_NameAndType_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class NameAndTypeConstant extends Constant
{
	public static final int TAG = 12; // JVMS::CONSTANT_NameAndType_info
	public static final ConstantKind KIND = new ConstantKind( TAG, "NameAndType" )
	{
		@Override public Buffer readBuffer( BufferReader bufferReader )
		{
			return bufferReader.readBuffer( 4 );
		}

		@Override public Constant parse( ConstantPool constantPool, BufferReader bufferReader )
		{
			return new NameAndTypeConstant( constantPool, bufferReader );
		}
	};

	public final Utf8Constant nameConstant;
	public final Utf8Constant descriptorConstant;

	public NameAndTypeConstant( String name, Descriptor descriptor )
	{
		super( KIND );
		nameConstant = new Utf8Constant( name );
		descriptorConstant = new Utf8Constant( descriptor.toUtf8Constant() );
	}

	public NameAndTypeConstant( String name, String descriptor )
	{
		super( KIND );
		nameConstant = new Utf8Constant( name );
		descriptorConstant = new Utf8Constant( descriptor );
	}

	private NameAndTypeConstant( ConstantPool constantPool, BufferReader bufferReader )
	{
		super( KIND );
		nameConstant = constantPool.readIndexAndGetConstant( bufferReader ).asUtf8Constant();
		descriptorConstant = constantPool.readIndexAndGetConstant( bufferReader ).asUtf8Constant();
	}

	@Override public void intern( ConstantPool constantPool )
	{
		nameConstant.intern( constantPool );
		descriptorConstant.intern( constantPool );
		super.intern( constantPool );
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		nameConstant.writeIndex( constantPool, bufferWriter );
		descriptorConstant.writeIndex( constantPool, bufferWriter );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( "name = " ).append( nameConstant );
		builder.append( ", descriptor = " ).append( descriptorConstant );
		builder.append( ' ' );
	}

	@Deprecated @Override public NameAndTypeConstant asNameAndTypeConstant()
	{
		return this;
	}

	@Override public boolean equals( Object other )
	{
		if( other instanceof NameAndTypeConstant )
		{
			NameAndTypeConstant otherNameAndTypeConstant = (NameAndTypeConstant)other;
			return equalsNameAndTypeConstant( otherNameAndTypeConstant );
		}
		return false;
	}

	public boolean equalsNameAndTypeConstant( NameAndTypeConstant other )
	{
		if( !nameConstant.equalsUtf8Constant( other.nameConstant ) )
			return false;
		if( !descriptorConstant.equalsUtf8Constant( other.descriptorConstant ) )
			return false;
		return true;
	}

	@Override public int hashCode()
	{
		return Objects.hash( kind, nameConstant, descriptorConstant );
	}

	public Descriptor getDescriptor()
	{
		return Descriptor.from( descriptorConstant.getStringValue() );
	}
}
