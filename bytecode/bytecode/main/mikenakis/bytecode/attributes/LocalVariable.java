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
 * Represents an entry of the {@link LocalVariableTableAttribute}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class LocalVariable extends Printable
{
	private final CodeAttribute codeAttribute;
	public final AbsoluteInstructionReference startInstructionReference;
	public final AbsoluteInstructionReference endInstructionReference;
	public final Utf8Constant nameConstant;
	public final Utf8Constant descriptorConstant;
	public final int index;

	public LocalVariable( CodeAttribute codeAttribute, Instruction startInstructionReference, Instruction endInstructionReference, String name, String descriptor, int index )
	{
		this.codeAttribute = codeAttribute;
		this.startInstructionReference = new AbsoluteInstructionReference( codeAttribute, startInstructionReference );
		this.endInstructionReference = new AbsoluteInstructionReference( codeAttribute, endInstructionReference );
		nameConstant = new Utf8Constant( name );
		descriptorConstant = new Utf8Constant( descriptor );
		this.index = index;
	}

	public LocalVariable( CodeAttribute codeAttribute, BufferReader bufferReader )
	{
		this.codeAttribute = codeAttribute;
		startInstructionReference = new AbsoluteInstructionReference( codeAttribute, false, bufferReader );
		int length = bufferReader.readUnsignedShort();
		int endPc = startInstructionReference.getPc() + length;
		Instruction endInstruction = codeAttribute.getInstructionByPc( endPc );
		endInstructionReference = new AbsoluteInstructionReference( codeAttribute, endInstruction );
		nameConstant = codeAttribute.method.declaringType.constantPool.readIndexAndGetConstant( bufferReader ).asUtf8Constant();
		descriptorConstant = codeAttribute.method.declaringType.constantPool.readIndexAndGetConstant( bufferReader ).asUtf8Constant();
		index = bufferReader.readUnsignedShort();
	}

	public int getLength()
	{
		int startPc = startInstructionReference.getPc();
		int endPc = endInstructionReference.getPc();
		return endPc - startPc;
	}

	public void intern( ConstantPool constantPool )
	{
		nameConstant.intern( constantPool );
		descriptorConstant.intern( constantPool );
	}

	public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		startInstructionReference.write( false, bufferWriter );
		bufferWriter.writeUnsignedShort( getLength() );
		nameConstant.writeIndex( constantPool, bufferWriter );
		descriptorConstant.writeIndex( constantPool, bufferWriter );
		bufferWriter.writeUnsignedShort( index );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( "index = " ).append( index );
		builder.append( ", startPc = " ).append( startInstructionReference.getPc() );
		builder.append( ", length = " ).append( getLength() );
	}

	public void collectTargets( Consumer<Instruction> targetInstructionConsumer )
	{
		targetInstructionConsumer.accept( startInstructionReference.getTargetInstruction() );
		targetInstructionConsumer.accept( endInstructionReference.getTargetInstruction() );
	}
}
