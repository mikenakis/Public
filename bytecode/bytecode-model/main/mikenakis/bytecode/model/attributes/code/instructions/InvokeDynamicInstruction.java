package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.bytecode.model.constants.InvokeDynamicConstant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class InvokeDynamicInstruction extends Instruction
{
	public static InvokeDynamicInstruction of( InvokeDynamicConstant invokeDynamicConstant )
	{
		return new InvokeDynamicInstruction( invokeDynamicConstant );
	}

	public final InvokeDynamicConstant invokeDynamicConstant;

	private InvokeDynamicInstruction( InvokeDynamicConstant invokeDynamicConstant )
	{
		super( Group.InvokeDynamic );
		this.invokeDynamicConstant = invokeDynamicConstant;
	}

	@Deprecated @Override public InvokeDynamicInstruction asInvokeDynamicInstruction()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return OpCode.getOpCodeName( OpCode.INVOKEDYNAMIC );
	}
}
