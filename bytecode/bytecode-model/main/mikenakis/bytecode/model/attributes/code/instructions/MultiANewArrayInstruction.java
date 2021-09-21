package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class MultiANewArrayInstruction extends Instruction
{
	public static MultiANewArrayInstruction of( ClassConstant classConstant, int dimensionCount )
	{
		return new MultiANewArrayInstruction( classConstant, dimensionCount );
	}

	public final ClassConstant classConstant;
	public final int dimensionCount;

	private MultiANewArrayInstruction( ClassConstant classConstant, int dimensionCount )
	{
		super( Group.MultiANewArray );
		this.classConstant = classConstant;
		this.dimensionCount = dimensionCount;
	}

	@Deprecated @Override public MultiANewArrayInstruction asMultiANewArrayInstruction()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return OpCode.getOpCodeName( OpCode.MULTIANEWARRAY );
	}
}
