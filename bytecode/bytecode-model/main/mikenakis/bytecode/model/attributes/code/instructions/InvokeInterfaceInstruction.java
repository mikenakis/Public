package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.bytecode.model.constants.InterfaceMethodReferenceConstant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class InvokeInterfaceInstruction extends Instruction
{
	public static InvokeInterfaceInstruction of( InterfaceMethodReferenceConstant interfaceMethodReferenceConstant, int argumentCount )
	{
		return new InvokeInterfaceInstruction( interfaceMethodReferenceConstant, argumentCount );
	}

	public final InterfaceMethodReferenceConstant interfaceMethodReferenceConstant;
	public final int argumentCount;

	private InvokeInterfaceInstruction( InterfaceMethodReferenceConstant interfaceMethodReferenceConstant, int argumentCount )
	{
		super( groupTag_InvokeInterface );
		this.interfaceMethodReferenceConstant = interfaceMethodReferenceConstant;
		this.argumentCount = argumentCount;
	}

	@Deprecated @Override public InvokeInterfaceInstruction asInvokeInterfaceInstruction()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return OpCode.getOpCodeName( OpCode.INVOKEINTERFACE );
	}
}
