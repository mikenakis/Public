package mikenakis.bytecode.attributes;

import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.attributes.code.AbsoluteInstructionReference;
import mikenakis.bytecode.attributes.code.Instruction;
import mikenakis.bytecode.constants.Utf8Constant;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.kit.Printable;

import java.util.function.Consumer;

/**
 * Represents an entry of the {@link LocalVariableTypeTableAttribute}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class LocalVariableType extends Printable
{
	public final AbsoluteInstructionReference startPc;
	public final int length;
	public final Utf8Constant nameConstant;
	public final Utf8Constant signatureConstant;
	public final int index;

	public LocalVariableType( AbsoluteInstructionReference startPc, int length, Utf8Constant nameConstant, Utf8Constant signatureConstant, int index )
	{
		this.startPc = startPc;
		this.length = length;
		this.nameConstant = nameConstant;
		this.signatureConstant = signatureConstant;
		this.index = index;
	}

	public LocalVariableType( CodeAttribute codeAttribute, BufferReader bufferReader )
	{
		startPc = new AbsoluteInstructionReference( codeAttribute, false, bufferReader );
		length = bufferReader.readUnsignedShort();
		nameConstant = codeAttribute.constantPool.readIndexAndGetConstant( bufferReader ).asUtf8Constant();
		signatureConstant = codeAttribute.constantPool.readIndexAndGetConstant( bufferReader ).asUtf8Constant();
		index = bufferReader.readUnsignedShort();
	}

	public void intern( ConstantPool constantPool )
	{
		nameConstant.intern( constantPool );
		signatureConstant.intern( constantPool );
	}

	public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		startPc.write( false, bufferWriter );
		bufferWriter.writeUnsignedShort( length );
		nameConstant.writeIndex( constantPool, bufferWriter );
		signatureConstant.writeIndex( constantPool, bufferWriter );
		bufferWriter.writeUnsignedShort( index );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( "index = " ).append( index );
		builder.append( ", startPc = " ).append( startPc.getPc() );
		builder.append( ", length = " ).append( length );
		builder.append( ", name = " ).append( nameConstant.getStringValue() );
		builder.append( ", signature = " ).append( signatureConstant.getStringValue() );
	}

	public void collectTargets( Consumer<Instruction> targetInstructionConsumer )
	{
		targetInstructionConsumer.accept( startPc.getTargetInstruction() );
	}
}
