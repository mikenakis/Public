package io.github.mikenakis.bytecode.model.attributes.code.instructions;

import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.kit.Helpers;
import io.github.mikenakis.bytecode.model.attributes.code.Instruction;
import io.github.mikenakis.bytecode.model.attributes.code.OpCode;
import io.github.mikenakis.bytecode.reading.ReadingLocationMap;
import io.github.mikenakis.bytecode.writing.InstructionWriter;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class ConditionalBranchInstruction extends Instruction
{
	public static ConditionalBranchInstruction read( BufferReader bufferReader, ReadingLocationMap locationMap, boolean wide, int opCode )
	{
		assert !wide;
		ConditionalBranchInstruction instruction = of( opCode );
		int targetInstructionOffset = bufferReader.readSignedShort();
		locationMap.setRelativeTargetInstruction( instruction, targetInstructionOffset, instruction::setTargetInstruction );
		return instruction;
	}

	public static ConditionalBranchInstruction of( int opCode )
	{
		return new ConditionalBranchInstruction( opCode );
	}

	private static boolean isValidOpCode( int opCode )
	{
		return switch( opCode )
			{
				case OpCode.IFEQ, OpCode.IFNONNULL, OpCode.IFNULL, OpCode.IF_ACMPNE, OpCode.IF_ACMPEQ, OpCode.IF_ICMPLE, OpCode.IF_ICMPGT, OpCode.IF_ICMPGE, //
					OpCode.IF_ICMPLT, OpCode.IF_ICMPNE, OpCode.IF_ICMPEQ, OpCode.IFLE, OpCode.IFGT, OpCode.IFGE, OpCode.IFLT, OpCode.IFNE -> true;
				default -> false;
			};
	}

	public final int opCode;
	private Instruction targetInstruction = null; //null means that it has not been set yet.

	private ConditionalBranchInstruction( int opCode )
	{
		super( groupTag_ConditionalBranch );
		assert isValidOpCode( opCode );
		this.opCode = opCode;
	}

	public Instruction getTargetInstruction()
	{
		assert targetInstruction != null;
		return targetInstruction;
	}

	public void setTargetInstruction( Instruction targetInstruction )
	{
		assert this.targetInstruction == null;
		assert targetInstruction != null;
		this.targetInstruction = targetInstruction;
	}

	@Deprecated @Override public ConditionalBranchInstruction asConditionalBranchInstruction() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return OpCode.getOpCodeName( opCode ); }

	@Override public void intern( Interner interner )
	{
		// nothing to do
	}

	@Override public void write( InstructionWriter instructionWriter )
	{
		int targetInstructionOffset = instructionWriter.getOffset( this, getTargetInstruction() );
		if( Helpers.isSignedShort( targetInstructionOffset ) )
		{
			instructionWriter.writeUnsignedByte( opCode );
			instructionWriter.writeSignedShort( targetInstructionOffset );
		}
		else
		{
			instructionWriter.writeUnsignedByte( getReverseOpCode( opCode ) );
			instructionWriter.writeSignedByte( 3 + 5 ); //length of this instruction plus length of GOTO_W instruction that follows
			if( targetInstructionOffset < 0 )
				targetInstructionOffset -= 3;
			else
				targetInstructionOffset += 2;
			instructionWriter.writeUnsignedByte( OpCode.GOTO_W );
			instructionWriter.writeInt( targetInstructionOffset );
		}
	}

	private static int getReverseOpCode( int opCode )
	{
		return switch( opCode )
			{
				case OpCode.IFEQ -> OpCode.IFNE;
				case OpCode.IFNE -> OpCode.IFEQ;
				case OpCode.IFLT -> OpCode.IFGE;
				case OpCode.IFGE -> OpCode.IFLT;
				case OpCode.IFGT -> OpCode.IFLE;
				case OpCode.IFLE -> OpCode.IFGT;
				case OpCode.IF_ICMPEQ -> OpCode.IF_ICMPNE;
				case OpCode.IF_ICMPNE -> OpCode.IF_ICMPEQ;
				case OpCode.IF_ICMPLT -> OpCode.IF_ICMPGE;
				case OpCode.IF_ICMPGE -> OpCode.IF_ICMPLT;
				case OpCode.IF_ICMPGT -> OpCode.IF_ICMPLE;
				case OpCode.IF_ICMPLE -> OpCode.IF_ICMPGT;
				case OpCode.IF_ACMPEQ -> OpCode.IF_ACMPNE;
				case OpCode.IF_ACMPNE -> OpCode.IF_ACMPEQ;
				case OpCode.IFNULL -> OpCode.IFNONNULL;
				case OpCode.IFNONNULL -> OpCode.IFNULL;
				default -> throw new AssertionError( opCode );
			};
	}
}
