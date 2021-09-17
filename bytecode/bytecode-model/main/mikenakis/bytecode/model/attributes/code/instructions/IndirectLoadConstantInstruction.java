package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.constants.DoubleConstant;
import mikenakis.bytecode.model.constants.FloatConstant;
import mikenakis.bytecode.model.constants.IntegerConstant;
import mikenakis.bytecode.model.constants.LongConstant;
import mikenakis.bytecode.model.constants.StringConstant;

public final class IndirectLoadConstantInstruction extends LoadConstantInstruction
{
	public static IndirectLoadConstantInstruction of( int opCode, Constant constant )
	{
		switch( constant.tag )
		{
			case IntegerConstant.TAG:
			case LongConstant.TAG:
			case FloatConstant.TAG:
			case DoubleConstant.TAG:
			case StringConstant.TAG:
			case ClassConstant.TAG:
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
