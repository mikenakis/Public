package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class OperandlessLoadConstantInstruction extends LoadConstantInstruction
{
	public static OperandlessLoadConstantInstruction of( int opCode )
	{
		return new OperandlessLoadConstantInstruction( opCode );
	}

	public final int opCode;

	private OperandlessLoadConstantInstruction( int opCode )
	{
		super( groupTag_OperandlessLoadConstant );
		assert OpCode.isImmediateLoadConstant( opCode );
		this.opCode = opCode;
	}

	public int getOpCode()
	{
		return opCode;
	}

	@Deprecated @Override public OperandlessLoadConstantInstruction asOperandlessLoadConstantInstruction()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return OpCode.getOpCodeName( opCode );
	}
}
