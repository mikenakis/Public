package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.code.OpCode;
import mikenakis.bytecode.attributes.code.instructions.IndirectLoadConstantInstruction;
import mikenakis.bytecode.dumping.RenderingContext;

/**
 * {@link IndirectLoadConstantInstruction} {@link InstructionPrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class IndirectLoadConstantInstructionPrinter extends InstructionPrinter
{
	private final IndirectLoadConstantInstruction indirectLoadConstantInstruction;

	public IndirectLoadConstantInstructionPrinter( IndirectLoadConstantInstruction indirectLoadConstantInstruction )
	{
		super( indirectLoadConstantInstruction );
		this.indirectLoadConstantInstruction = indirectLoadConstantInstruction;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		super.appendTo( renderingContext, builder );
		builder.append( !renderingContext.style.raw ? "LDCx" : OpCode.getOpCodeName( indirectLoadConstantInstruction.model.opCode ) );
		builder.append( ' ' );
		RenderingContext.newPrinter( indirectLoadConstantInstruction.constant ).appendIndexTo( renderingContext, builder );
	}
}
