package io.github.mikenakis.bytecode.model.attributes.code.instructions;

import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.kit.Helpers;
import io.github.mikenakis.bytecode.model.attributes.code.Instruction;
import io.github.mikenakis.bytecode.model.attributes.code.OpCode;
import io.github.mikenakis.bytecode.reading.ReadingLocationMap;
import io.github.mikenakis.bytecode.writing.InstructionWriter;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class BranchInstruction extends Instruction
{
	public static BranchInstruction read( BufferReader bufferReader, ReadingLocationMap locationMap, boolean wide, int opCode )
	{
		assert !wide;
		BranchInstruction instruction = of( opCode );
		int targetInstructionOffset = isLong( opCode ) ? bufferReader.readInt() : bufferReader.readSignedShort();
		locationMap.setRelativeTargetInstruction( instruction, targetInstructionOffset, instruction::setTargetInstruction );
		return instruction;
	}

	public static BranchInstruction of( int opCode )
	{
		return new BranchInstruction( generalFromSpecialOpcode( opCode ) );
	}

	public final int opCode;
	private Instruction targetInstruction; //null means that it has not been set yet.

	private BranchInstruction( int opCode )
	{
		super( groupTag_Branch );
		assert opCode == OpCode.GOTO || opCode == OpCode.JSR;
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

	@Deprecated @Override public BranchInstruction asBranchInstruction() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return OpCode.getOpCodeName( opCode ); }

	@Override public void intern( Interner interner )
	{
		// nothing to do
	}

	@Override public void write( InstructionWriter instructionWriter )
	{
		int offset = instructionWriter.getOffset( this, getTargetInstruction() );
		boolean isLong = !Helpers.isSignedShort( offset );
		instructionWriter.writeUnsignedByte( getOpCode( isLong ) );
		if( isLong )
			instructionWriter.writeInt( offset );
		else
			instructionWriter.writeSignedShort( offset );
	}

	private int getOpCode( boolean isLong )
	{
		if( isLong )
			return switch( opCode )
				{
					case OpCode.GOTO -> OpCode.GOTO_W;
					case OpCode.JSR -> OpCode.JSR_W;
					default -> throw new AssertionError( opCode );
				};
		return opCode;
	}

	private static int generalFromSpecialOpcode( int opcode )
	{
		return switch( opcode )
			{
				case OpCode.GOTO, OpCode.JSR -> opcode;
				case OpCode.GOTO_W -> OpCode.GOTO;
				case OpCode.JSR_W -> OpCode.JSR;
				default -> throw new AssertionError( opcode );
			};
	}

	private static boolean isLong( int opCode )
	{
		return switch( opCode )
			{
				case OpCode.GOTO, OpCode.JSR -> false;
				case OpCode.GOTO_W, OpCode.JSR_W -> true;
				default -> throw new AssertionError( opCode );
			};
	}
}
