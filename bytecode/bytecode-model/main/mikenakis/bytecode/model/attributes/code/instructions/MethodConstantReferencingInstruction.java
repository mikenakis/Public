package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.bytecode.model.constants.MethodReferenceConstant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Set;

public final class MethodConstantReferencingInstruction extends Instruction
{
	public static MethodConstantReferencingInstruction of( int opCode, MethodReferenceConstant methodReferenceConstant )
	{
		return new MethodConstantReferencingInstruction( opCode, methodReferenceConstant );
	}

	private static final Set<Integer> opCodes = Set.of( OpCode.INVOKEVIRTUAL, OpCode.INVOKESPECIAL, OpCode.INVOKESTATIC );

	public final int opCode;
	public final MethodReferenceConstant methodReferenceConstant;

	private MethodConstantReferencingInstruction( int opCode, MethodReferenceConstant methodReferenceConstant )
	{
		super( groupTag_MethodConstantReferencing );
		assert methodReferenceConstant.tag == Constant.tag_PlainMethodReference || methodReferenceConstant.tag == Constant.tag_InterfaceMethodReference;
		assert opCodes.contains( opCode );
		this.opCode = opCode;
		this.methodReferenceConstant = methodReferenceConstant;
	}

	@Deprecated @Override public MethodConstantReferencingInstruction asMethodConstantReferencingInstruction() { return this; }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return OpCode.getOpCodeName( opCode );
	}
}
