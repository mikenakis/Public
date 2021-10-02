package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.descriptors.ArrayTypeDescriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class MultiANewArrayInstruction extends Instruction
{
	public static MultiANewArrayInstruction of( ClassConstant targetClassConstant, int dimensionCount )
	{
		return new MultiANewArrayInstruction( targetClassConstant, dimensionCount );
	}

	public final ClassConstant targetClassConstant;
	public final int dimensionCount;

	private MultiANewArrayInstruction( ClassConstant targetClassConstant, int dimensionCount )
	{
		super( groupTag_MultiANewArray );
		this.targetClassConstant = targetClassConstant;
		this.dimensionCount = dimensionCount;
	}

	public ArrayTypeDescriptor targetType() { return targetClassConstant.arrayTypeDescriptor(); }
	@Deprecated @Override public MultiANewArrayInstruction asMultiANewArrayInstruction() { return this; }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return OpCode.getOpCodeName( OpCode.MULTIANEWARRAY ) + " " + targetType().typeName();
	}
}
