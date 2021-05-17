package mikenakis.bytecode.constants;

import mikenakis.bytecode.ByteCodeType;
import mikenakis.bytecode.Constant;
import mikenakis.bytecode.ConstantKind;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.attributes.BootstrapMethod;
import mikenakis.bytecode.kit.Buffer;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

import java.util.Objects;

/**
 * Represents the JVMS::CONSTANT_InvokeDynamic_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class InvokeDynamicConstant extends Constant
{
	public static final int TAG = 18; // JVMS::CONSTANT_InvokeDynamic_info
	public static final ConstantKind KIND = new ConstantKind( TAG, "InvokeDynamic" )
	{
		@Override public Buffer readBuffer( BufferReader bufferReader )
		{
			return bufferReader.readBuffer( 4 );
		}

		@Override public Constant parse( ConstantPool constantPool, BufferReader bufferReader )
		{
			return new InvokeDynamicConstant( constantPool, bufferReader );
		}
	};

	private final ByteCodeType byteCodeType;
	public final int bootstrapMethodIndex;
	public final NameAndTypeConstant nameAndTypeConstant;

	public InvokeDynamicConstant( ConstantPool constantPool, BootstrapMethod bootstrapMethod, NameAndTypeConstant nameAndTypeConstant )
	{
		super( KIND );
		byteCodeType = constantPool.byteCodeType;
		bootstrapMethodIndex = byteCodeType.getIndexOfBootstrapMethod( bootstrapMethod );
		this.nameAndTypeConstant = nameAndTypeConstant;
	}

	private InvokeDynamicConstant( ConstantPool constantPool, BufferReader bufferReader )
	{
		super( KIND );
		byteCodeType = constantPool.byteCodeType;
		bootstrapMethodIndex = bufferReader.readUnsignedShort();
		nameAndTypeConstant = constantPool.readIndexAndGetConstant( bufferReader ).asNameAndTypeConstant();
	}

	@Override public void intern( ConstantPool constantPool )
	{
		nameAndTypeConstant.intern( constantPool );
		super.intern( constantPool );
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		bufferWriter.writeUnsignedShort( bootstrapMethodIndex );
		nameAndTypeConstant.writeIndex( constantPool, bufferWriter );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( "bootstrapMethod " ).append( bootstrapMethodIndex );
		builder.append( ", nameAndType = " );
		nameAndTypeConstant.toStringBuilder( builder );
	}

	@Deprecated @Override public InvokeDynamicConstant asInvokeDynamicConstant()
	{
		return this;
	}

	@Override public boolean equals( Object other )
	{
		if( other instanceof InvokeDynamicConstant )
		{
			InvokeDynamicConstant otherInvokeDynamicConstant = (InvokeDynamicConstant)other;
			if( bootstrapMethodIndex != otherInvokeDynamicConstant.bootstrapMethodIndex )
				return false;
			if( !nameAndTypeConstant.equalsNameAndTypeConstant( otherInvokeDynamicConstant.nameAndTypeConstant ) )
				return false;
			return true;
		}
		return false;
	}

	@Override public int hashCode()
	{
		return Objects.hash( kind, bootstrapMethodIndex, nameAndTypeConstant );
	}

	public BootstrapMethod getBootstrapMethod()
	{
		return byteCodeType.getBootstrapMethodByIndex( bootstrapMethodIndex );
	}
}
