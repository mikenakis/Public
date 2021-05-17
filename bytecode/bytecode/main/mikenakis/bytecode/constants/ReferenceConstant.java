package mikenakis.bytecode.constants;

import mikenakis.bytecode.Constant;
import mikenakis.bytecode.ConstantKind;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.kit.Buffer;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

import java.util.Objects;

/**
 * Base class for representing the JVMS::CONSTANT_Fieldref_info, JVMS::CONSTANT_Methodref_info, and JVMS::CONSTANT_InterfaceMethodref_info structures.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class ReferenceConstant extends Constant
{
	abstract static class Kind extends ConstantKind
	{
		Kind( int tag, String name )
		{
			super( tag, name );
		}

		@Override public Buffer readBuffer( BufferReader bufferReader )
		{
			return bufferReader.readBuffer( 4 );
		}

		@Override public abstract Constant parse( ConstantPool constantPool, BufferReader bufferReader );
	}

	public final ClassConstant typeConstant;
	public final NameAndTypeConstant nameAndTypeConstant;

	protected ReferenceConstant( ConstantKind kind, ClassConstant typeConstant, NameAndTypeConstant nameAndTypeConstant )
	{
		super( kind );
		assert kind == FieldReferenceConstant.KIND || kind == PlainMethodReferenceConstant.KIND || kind == InterfaceMethodReferenceConstant.KIND;
		this.typeConstant = typeConstant;
		this.nameAndTypeConstant = nameAndTypeConstant;
	}

	protected ReferenceConstant( ConstantPool constantPool, ConstantKind kind, BufferReader bufferReader )
	{
		super( kind );
		assert kind == FieldReferenceConstant.KIND || kind == PlainMethodReferenceConstant.KIND || kind == InterfaceMethodReferenceConstant.KIND;
		typeConstant = constantPool.readIndexAndGetConstant( bufferReader ).asClassConstant();
		nameAndTypeConstant = constantPool.readIndexAndGetConstant( bufferReader ).asNameAndTypeConstant();
	}

	@Override public final void intern( ConstantPool constantPool )
	{
		typeConstant.intern( constantPool );
		nameAndTypeConstant.intern( constantPool );
		super.intern( constantPool );
	}

	@Override public final void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		typeConstant.writeIndex( constantPool, bufferWriter );
		nameAndTypeConstant.writeIndex( constantPool, bufferWriter );
	}

	@Override public final void toStringBuilder( StringBuilder builder )
	{
		builder.append( "type = " );
		typeConstant.toStringBuilder( builder );
		builder.append( ", nameAndType = " );
		nameAndTypeConstant.toStringBuilder( builder );
	}

	@Deprecated @Override public final ReferenceConstant asReferenceConstant()
	{
		return this;
	}

	@Override public final boolean equals( Object other )
	{
		if( other instanceof ReferenceConstant )
		{
			ReferenceConstant otherReferenceConstant = (ReferenceConstant)other;
			return equalsReferenceConstant( otherReferenceConstant );
		}
		return false;
	}

	public boolean equalsReferenceConstant( ReferenceConstant other )
	{
		if( !typeConstant.equalsClassConstant( other.typeConstant ) )
			return false;
		if( !nameAndTypeConstant.equalsNameAndTypeConstant( other.nameAndTypeConstant ) )
			return false;
		return true;
	}

	@Override public final int hashCode()
	{
		return Objects.hash( kind, typeConstant, nameAndTypeConstant );
	}
}
