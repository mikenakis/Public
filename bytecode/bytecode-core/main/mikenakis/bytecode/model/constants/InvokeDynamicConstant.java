package mikenakis.bytecode.model.constants;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.attributes.BootstrapMethod;
import mikenakis.bytecode.model.descriptors.MethodPrototype;
import mikenakis.bytecode.reading.ReadingConstantPool;
import mikenakis.bytecode.writing.Interner;
import mikenakis.bytecode.writing.WritingBootstrapPool;
import mikenakis.bytecode.writing.WritingConstantPool;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Objects;

/**
 * Represents the JVMS::CONSTANT_InvokeDynamic_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class InvokeDynamicConstant extends Constant
{
	public static InvokeDynamicConstant read( BufferReader bufferReader, ReadingConstantPool constantPool, int constantTag )
	{
		assert constantTag == tag_InvokeDynamic;
		InvokeDynamicConstant invokeDynamicConstant = new InvokeDynamicConstant();
		constantPool.setBootstrap( bufferReader.readUnsignedShort(), b -> invokeDynamicConstant.setBootstrapMethod( b ) );
		constantPool.setConstant( bufferReader.readUnsignedShort(), c -> invokeDynamicConstant.setNameAndDescriptorConstant( c.asNameAndDescriptorConstant() ) );
		return invokeDynamicConstant;
	}

	public static InvokeDynamicConstant of( BootstrapMethod bootstrapMethod, NameAndDescriptorConstant nameAndDescriptorConstant )
	{
		InvokeDynamicConstant invokeDynamicConstant = new InvokeDynamicConstant();
		invokeDynamicConstant.setBootstrapMethod( bootstrapMethod );
		invokeDynamicConstant.setNameAndDescriptorConstant( nameAndDescriptorConstant );
		return invokeDynamicConstant;
	}

	private NameAndDescriptorConstant nameAndDescriptorConstant; // null means that it has not been set yet.
	private BootstrapMethod bootstrapMethod; // null means that it has not been set yet.

	public InvokeDynamicConstant()
	{
		super( tag_InvokeDynamic );
	}

	public BootstrapMethod getBootstrapMethod()
	{
		assert bootstrapMethod != null;
		return bootstrapMethod;
	}

	public void setBootstrapMethod( BootstrapMethod bootstrapMethod )
	{
		assert this.bootstrapMethod == null;
		assert bootstrapMethod != null;
		this.bootstrapMethod = bootstrapMethod;
	}

	public NameAndDescriptorConstant getNameAndDescriptorConstant()
	{
		assert nameAndDescriptorConstant != null;
		return nameAndDescriptorConstant;
	}

	public void setNameAndDescriptorConstant( NameAndDescriptorConstant nameAndDescriptorConstant )
	{
		assert this.nameAndDescriptorConstant == null;
		assert nameAndDescriptorConstant != null;
		this.nameAndDescriptorConstant = nameAndDescriptorConstant;
	}

	public MethodPrototype methodPrototype() { return ByteCodeHelpers.methodPrototypeFromNameAndDescriptorConstant( nameAndDescriptorConstant ); }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "bootstrapMethod = {" + bootstrapMethod + "}; nameAndDescriptor = " + nameAndDescriptorConstant; }
	@Deprecated @Override public InvokeDynamicConstant asInvokeDynamicConstant() { return this; }
	@Deprecated @Override public boolean equals( Object other ) { return other instanceof InvokeDynamicConstant kin && equals( kin ); }
	public boolean equals( InvokeDynamicConstant other ) { return nameAndDescriptorConstant.equals( other.nameAndDescriptorConstant ) && getBootstrapMethod().equals( other.getBootstrapMethod() ); }
	@Override public int hashCode() { return Objects.hash( tag, nameAndDescriptorConstant, getBootstrapMethod() ); }

	@Override public void intern( Interner interner )
	{
		interner.intern( this );
		getNameAndDescriptorConstant().intern( interner );
	}

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, WritingBootstrapPool bootstrapPool )
	{
		bufferWriter.writeUnsignedByte( tag );
		int bootstrapMethodIndex = bootstrapPool.getBootstrapIndex( getBootstrapMethod() );
		bufferWriter.writeUnsignedShort( bootstrapMethodIndex );
		bufferWriter.writeUnsignedShort( constantPool.getConstantIndex( getNameAndDescriptorConstant() ) );
	}
}
