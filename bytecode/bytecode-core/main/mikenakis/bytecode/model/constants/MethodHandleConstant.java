package mikenakis.bytecode.model.constants;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.reading.ReadingConstantPool;
import mikenakis.bytecode.writing.Interner;
import mikenakis.bytecode.writing.WritingBootstrapPool;
import mikenakis.bytecode.writing.WritingConstantPool;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.lang.constant.DirectMethodHandleDesc;
import java.util.Objects;

/**
 * Represents the JVMS::CONSTANT_MethodHandle_info structure.
 *
 * @author michael.gr
 */
public final class MethodHandleConstant extends Constant
{
	public static MethodHandleConstant read( BufferReader bufferReader, ReadingConstantPool constantPool, int constantTag )
	{
		assert constantTag == tag_MethodHandle;
		int referenceKindNumber = bufferReader.readUnsignedByte();
		ReferenceKind referenceKind = ReferenceKind.tryFromNumber( referenceKindNumber ).orElseThrow();
		MethodHandleConstant methodHandleConstant = new MethodHandleConstant( referenceKind );
		constantPool.setConstant( bufferReader.readUnsignedShort(), c -> methodHandleConstant.setReferenceConstant( c.asReferenceConstant() ) );
		return methodHandleConstant;
	}

	private final ReferenceKind referenceKind;
	private ReferenceConstant referenceConstant; //null means it has not been set yet.

	public MethodHandleConstant( ReferenceKind referenceKind )
	{
		super( tag_MethodHandle );
		this.referenceKind = referenceKind;
	}

	public ReferenceKind referenceKind() { return referenceKind; }

	public ReferenceConstant getReferenceConstant()
	{
		assert referenceConstant != null;
		return referenceConstant;
	}

	public void setReferenceConstant( ReferenceConstant referenceConstant )
	{
		assert this.referenceConstant == null;
		assert referenceConstant != null;
		assert referenceConstant.tag == tag_FieldReference || referenceConstant.tag == tag_PlainMethodReference || referenceConstant.tag == tag_InterfaceMethodReference;
		this.referenceConstant = referenceConstant;
	}

	@Deprecated @Override public MethodHandleConstant asMethodHandleConstant() { return this; }
	public DirectMethodHandleDesc directMethodHandleDesc() { return ByteCodeHelpers.directMethodHandleDescFromMethodHandleConstant( this ); }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "kind = " + referenceKind.name() + ", referenceConstant = " + referenceConstant; }
	@Deprecated @Override public boolean equals( Object other ) { return other instanceof MethodHandleConstant kin && equals( kin ); }
	public boolean equals( MethodHandleConstant other ) { return referenceKind == other.referenceKind && referenceConstant.equals( other.referenceConstant ); }
	@Override public int hashCode() { return Objects.hash( tag, referenceKind, referenceConstant ); }

	@Override public void intern( Interner interner )
	{
		interner.intern( this );
		getReferenceConstant().intern( interner );
	}

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, WritingBootstrapPool bootstrapPool )
	{
		bufferWriter.writeUnsignedByte( tag );
		bufferWriter.writeUnsignedByte( referenceKind().number );
		bufferWriter.writeUnsignedShort( constantPool.getConstantIndex( getReferenceConstant() ) );
	}
}
