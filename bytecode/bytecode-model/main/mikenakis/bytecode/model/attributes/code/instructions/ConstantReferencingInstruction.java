package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.exceptions.InvalidConstantTagException;
import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Set;

public final class ConstantReferencingInstruction extends Instruction
{
	public static ConstantReferencingInstruction of( int opCode, Constant constant )
	{
		return new ConstantReferencingInstruction( opCode, constant );
	}

	private static final Set<Integer> opCodes = Set.of( OpCode.GETSTATIC, OpCode.PUTSTATIC, OpCode.GETFIELD, OpCode.PUTFIELD, OpCode.INVOKEVIRTUAL, //
		OpCode.INVOKESPECIAL, OpCode.INVOKESTATIC, OpCode.NEW, OpCode.ANEWARRAY, OpCode.CHECKCAST, OpCode.INSTANCEOF );

	public final int opCode;
	public final Constant constant;

	private ConstantReferencingInstruction( int opCode, Constant constant )
	{
		super( Group.ConstantReferencing );
		switch( constant.tag )
		{
			case Constant.tagFieldReference:
			case Constant.tagInterfaceMethodReference:
			case Constant.tagMethodReference:
			case Constant.tagClass:
				break;
			default:
				throw new AssertionError( constant );
		}
		assert opCodes.contains( opCode );
		this.opCode = opCode;
		this.constant = constant;
	}

	@Deprecated @Override public ConstantReferencingInstruction asConstantReferencingInstruction()
	{
		return this;
	}

	public int getOpCode()
	{
		return opCode;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return OpCode.getOpCodeName( opCode );
	}
}
