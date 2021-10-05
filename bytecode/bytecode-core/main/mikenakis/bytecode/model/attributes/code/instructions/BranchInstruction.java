package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class BranchInstruction extends Instruction
{
	public static BranchInstruction of( int opCode )
	{
		return new BranchInstruction( generalFromSpecialOpcode( opCode ) );
	}

	public static boolean isLong( int opCode )
	{
		return switch( opCode )
			{
				case OpCode.GOTO, OpCode.JSR -> false;
				case OpCode.GOTO_W, OpCode.JSR_W -> true;
				default -> throw new AssertionError( opCode );
			};
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

	public int getOpCode( boolean isLong )
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

	@Deprecated @Override public BranchInstruction asBranchInstruction() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return OpCode.getOpCodeName( opCode ); }

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
}
