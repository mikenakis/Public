package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.kit.Helpers;
import mikenakis.bytecode.model.attributes.code.OpCode;

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
		super( Group.ImmediateLoadConstant );
		assert (Helpers.isUnsignedByte( immediateValue ) && opCode == OpCode.BIPUSH) || //
			(Helpers.isUnsignedShort( immediateValue ) && opCode == OpCode.SIPUSH);
		this.opCode = opCode;
		this.immediateValue = immediateValue;
	}

	@Override public int getOpCode()
	{
		return opCode;
	}

	@Deprecated @Override public ImmediateLoadConstantInstruction asImmediateLoadConstantInstruction()
	{
		return this;
	}
}
