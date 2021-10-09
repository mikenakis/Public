package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.reading.ReadingConstantPool;
import mikenakis.bytecode.writing.InstructionWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.java_type_model.ArrayTypeDescriptor;
import mikenakis.java_type_model.TerminalTypeDescriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class MultiANewArrayInstruction extends Instruction
{
	public static MultiANewArrayInstruction read( BufferReader bufferReader, ReadingConstantPool constantPool, boolean wide, int opCode )
	{
		assert !wide;
		assert opCode == OpCode.MULTIANEWARRAY;
		ClassConstant targetClassConstant = constantPool.getConstant( bufferReader.readUnsignedShort() ).asClassConstant();
		int dimensionCount = bufferReader.readUnsignedByte();
		return new MultiANewArrayInstruction( targetClassConstant, dimensionCount );
	}

	public static MultiANewArrayInstruction of( TerminalTypeDescriptor primitiveTypeDescriptor, int dimensionCount )
	{
		ClassConstant targetClassConstant = ClassConstant.of( primitiveTypeDescriptor );
		return new MultiANewArrayInstruction( targetClassConstant, dimensionCount );
	}

	private final ClassConstant targetClassConstant;
	public final int dimensionCount;

	private MultiANewArrayInstruction( ClassConstant targetClassConstant, int dimensionCount )
	{
		super( groupTag_MultiANewArray );
		this.targetClassConstant = targetClassConstant;
		this.dimensionCount = dimensionCount;
	}

	public ArrayTypeDescriptor targetType() { return targetClassConstant.arrayTypeDescriptor(); }
	@Deprecated @Override public MultiANewArrayInstruction asMultiANewArrayInstruction() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return OpCode.getOpCodeName( OpCode.MULTIANEWARRAY ) + " " + targetType().typeName(); }

	@Override public void intern( Interner interner )
	{
		targetClassConstant.intern( interner );
	}

	@Override public void write( InstructionWriter instructionWriter )
	{
		int constantIndex = instructionWriter.getIndex( targetClassConstant );
		instructionWriter.writeUnsignedByte( OpCode.MULTIANEWARRAY );
		instructionWriter.writeUnsignedShort( constantIndex );
		instructionWriter.writeUnsignedByte( dimensionCount );
	}
}
