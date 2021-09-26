package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.kit.Helpers;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class ImmediateLoadConstantInstruction extends LoadConstantInstruction
{
	public static ImmediateLoadConstantInstruction of( int opCode, int immediateValue )
	{
		return new ImmediateLoadConstantInstruction( opCode, immediateValue );
	}

	public final int opCode;
	public final int immediateValue;

	private ImmediateLoadConstantInstruction( int opCode, int immediateValue )
	{
		super( groupTag_ImmediateLoadConstant );
		assert (Helpers.isUnsignedByte( immediateValue ) && opCode == OpCode.BIPUSH) || //
			(Helpers.isUnsignedShort( immediateValue ) && opCode == OpCode.SIPUSH);
		this.opCode = opCode;
		this.immediateValue = immediateValue;
	}

	public int getOpCode()
	{
		return opCode;
	}

	@Deprecated @Override public ImmediateLoadConstantInstruction asImmediateLoadConstantInstruction()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return OpCode.getOpCodeName( opCode );
	}
}
