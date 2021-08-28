package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.code.OpCode;
import mikenakis.bytecode.attributes.code.instructions.ConstantReferencingInstruction;
import mikenakis.bytecode.dumping.RenderingContext;

/**
 * {@link ConstantReferencingInstruction} {@link InstructionPrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ConstantReferencingInstructionPrinter extends InstructionPrinter
{
	private final ConstantReferencingInstruction constantReferencingInstruction;

	public ConstantReferencingInstructionPrinter( ConstantReferencingInstruction constantReferencingInstruction )
	{
		super( constantReferencingInstruction );
		this.constantReferencingInstruction = constantReferencingInstruction;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		super.appendTo( renderingContext, builder );
		builder.append( OpCode.getOpCodeName( constantReferencingInstruction.model.opCode ) );
		builder.append( ' ' );
		RenderingContext.newPrinter( constantReferencingInstruction.constant ).appendIndexTo( renderingContext, builder );
	}
}
