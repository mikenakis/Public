package mikenakis.bytecode.model.constants;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import mikenakis.bytecode.reading.ReadingConstantPool;
import mikenakis.bytecode.writing.Interner;
import mikenakis.bytecode.writing.WritingBootstrapPool;
import mikenakis.bytecode.writing.WritingConstantPool;
import mikenakis.java_type_model.MethodDescriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Objects;

/**
 * Represents the JVMS::CONSTANT_MethodType_info structure.
 *
 * @author michael.gr
 */
public final class MethodTypeConstant extends Constant
{
	public static MethodTypeConstant read( BufferReader bufferReader, ReadingConstantPool constantPool, int constantTag )
	{
		assert constantTag == tag_MethodType;
		MethodTypeConstant methodTypeConstant = new MethodTypeConstant();
		constantPool.setConstant( bufferReader.readUnsignedShort(), c -> methodTypeConstant.setDescriptorConstant( c.asMutf8ValueConstant() ) );
		return methodTypeConstant;
	}

	private Mutf8ValueConstant descriptorConstant; //null means that it has not been set yet.

	public MethodTypeConstant()
	{
		super( tag_MethodType );
	}

	public Mutf8ValueConstant getDescriptorConstant()
	{
		assert descriptorConstant != null;
		return descriptorConstant;
	}

	public void setDescriptorConstant( Mutf8ValueConstant descriptorConstant )
	{
		assert this.descriptorConstant == null;
		assert descriptorConstant != null;
		this.descriptorConstant = descriptorConstant;
	}

	public MethodDescriptor methodDescriptor() { return ByteCodeHelpers.methodDescriptorFromDescriptorString( descriptorConstant.stringValue() ); }
	@Deprecated @Override public MethodTypeConstant asMethodTypeConstant() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "descriptor = " + descriptorConstant; }
	@Deprecated @Override public boolean equals( Object other ) { return other instanceof MethodTypeConstant kin && equals( kin ); }
	public boolean equals( MethodTypeConstant other ) { return descriptorConstant.equals( other.descriptorConstant ); }
	@Override public int hashCode() { return Objects.hash( tag, descriptorConstant ); }

	@Override public void intern( Interner interner )
	{
		interner.intern( this );
		getDescriptorConstant().intern( interner );
	}

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, WritingBootstrapPool bootstrapPool )
	{
		bufferWriter.writeUnsignedByte( tag );
		bufferWriter.writeUnsignedShort( constantPool.getConstantIndex( getDescriptorConstant() ) );
	}
}
