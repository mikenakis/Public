package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.model.attributes.code.OpCode;

public final class OperandlessLoadConstantInstruction extends LoadConstantInstruction
{

	public static OperandlessLoadConstantInstruction of( int opCode )
	{
		return new OperandlessLoadConstantInstruction( opCode );
	}

	public final int opCode;

	private OperandlessLoadConstantInstruction( int opCode )
	{
		super( Group.OperandlessLoadConstant );
		assert OpCode.isImmediateLoadConstant( opCode );
		this.opCode = opCode;
	}

	@Override public int getOpCode()
	{
		return opCode;
	}

	@Deprecated @Override public OperandlessLoadConstantInstruction asOperandlessLoadConstantInstruction()
	{
		return this;
	}
}
