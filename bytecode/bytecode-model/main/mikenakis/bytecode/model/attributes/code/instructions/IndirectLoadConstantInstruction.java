package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class IndirectLoadConstantInstruction extends LoadConstantInstruction
{
	public static IndirectLoadConstantInstruction of( int opCode, Constant constant )
	{
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
		super( groupTag_IndirectLoadConstant );
		assert OpCode.isIndirectLoadConstant( opCode );
		assert isValidConstant( constant );
		this.opCode = opCode;
		this.constant = constant;
	}

	private static boolean isValidConstant( Constant constant )
	{
		return switch( constant.tag )
			{
				case Constant.tag_Integer, Constant.tag_Long, Constant.tag_Float, Constant.tag_Double, Constant.tag_String, Constant.tag_Class -> true;
				default -> false;
			};
	}

	public int getOpCode()
	{
		return opCode;
	}

	@Deprecated @Override public IndirectLoadConstantInstruction asIndirectLoadConstantInstruction()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return OpCode.getOpCodeName( opCode );
	}
}
