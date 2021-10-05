package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class ConditionalBranchInstruction extends Instruction
{
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
}
