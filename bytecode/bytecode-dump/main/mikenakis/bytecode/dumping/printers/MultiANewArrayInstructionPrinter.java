package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.code.OpCode;
import mikenakis.bytecode.attributes.code.instructions.MultiANewArrayInstruction;
import mikenakis.bytecode.dumping.RenderingContext;

/**
 * {@link MultiANewArrayInstruction} {@link InstructionPrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class MultiANewArrayInstructionPrinter extends InstructionPrinter
{
	private final MultiANewArrayInstruction multiANewArrayInstruction;

	public MultiANewArrayInstructionPrinter( MultiANewArrayInstruction multiANewArrayInstruction )
	{
		super( multiANewArrayInstruction );
		this.multiANewArrayInstruction = multiANewArrayInstruction;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		super.appendTo( renderingContext, builder );
		builder.append( OpCode.getOpCodeName( multiANewArrayInstruction.model.opCode ) );
		builder.append( ' ' );
		renderingContext.newPrinter( multiANewArrayInstruction.constant ).appendIndexTo( renderingContext, builder );
		builder.append( ' ' ).append( multiANewArrayInstruction.dimensionCount ).append( " dimensions" );
	}
}
