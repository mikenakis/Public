package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.attributes.code.OpCode;

public final class IndirectLoadConstantInstruction extends LoadConstantInstruction
{
	public static IndirectLoadConstantInstruction of( int opCode, Constant constant )
	{
		switch( constant.tag )
		{
			case Integer:
			case Long:
			case Float:
			case Double:
			case String:
			case Class:
				break;
			default:
				assert false;
				break;
		}
		return new IndirectLoadConstantInstruction( opCode, constant );
	}

	public static boolean isWide( int opCode )
	{
		return switch( opCode )
		{
			case OpCode.LDC -> false;
			case OpCode.LDC_W, OpCode.LDC2_W -> true;
			default -> throw new AssertionError( opCode );
		};
	}

	public final int opCode;
	public final Constant constant;

	private IndirectLoadConstantInstruction( int opCode, Constant constant )
	{
		super( Group.IndirectLoadConstant );
		assert OpCode.isIndirectLoadConstant( opCode );
		this.opCode = opCode;
		this.constant = constant;
	}

	@Override public int getOpCode()
	{
		return opCode;
	}

	@Deprecated @Override public IndirectLoadConstantInstruction asIndirectLoadConstantInstruction()
	{
		return this;
	}
}
