package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.code.OpCode;
import mikenakis.bytecode.attributes.code.instructions.ConditionalBranchInstruction;
import mikenakis.bytecode.dumping.RenderingContext;

/**
 * {@link ConditionalBranchInstruction} {@link InstructionPrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ConditionalBranchInstructionPrinter extends InstructionPrinter
{
	private final ConditionalBranchInstruction conditionalBranchInstruction;

	public ConditionalBranchInstructionPrinter( ConditionalBranchInstruction conditionalBranchInstruction )
	{
		super( conditionalBranchInstruction );
		this.conditionalBranchInstruction = conditionalBranchInstruction;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		super.appendTo( renderingContext, builder );
		builder.append( OpCode.getOpCodeName( conditionalBranchInstruction.model.kind.opCode ) );
		builder.append( ' ' );
		renderingContext.newPrinter( conditionalBranchInstruction.instructionReference ).appendTo( renderingContext, builder );
	}
}
