package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.code.OpCode;
import mikenakis.bytecode.attributes.code.instructions.BranchInstruction;
import mikenakis.bytecode.dumping.RenderingContext;

/**
 * {@link BranchInstruction} {@link InstructionPrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class BranchInstructionPrinter extends InstructionPrinter
{
	private final BranchInstruction branchInstruction;

	public BranchInstructionPrinter( BranchInstruction branchInstruction )
	{
		super( branchInstruction );
		this.branchInstruction = branchInstruction;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		super.appendTo( renderingContext, builder );
		builder.append( OpCode.getOpCodeName( branchInstruction.getOpCode() ) );
		builder.append( ' ' );
		renderingContext.newPrinter( branchInstruction.instructionReference ).appendTo( renderingContext, builder );
	}
}
