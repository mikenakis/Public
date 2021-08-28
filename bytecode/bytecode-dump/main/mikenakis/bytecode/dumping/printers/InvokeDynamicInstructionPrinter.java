package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.code.OpCode;
import mikenakis.bytecode.attributes.code.instructions.InvokeDynamicInstruction;
import mikenakis.bytecode.dumping.RenderingContext;

/**
 * {@link InvokeDynamicInstruction} {@link InstructionPrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class InvokeDynamicInstructionPrinter extends InstructionPrinter
{
	private final InvokeDynamicInstruction invokeDynamicInstruction;

	public InvokeDynamicInstructionPrinter( InvokeDynamicInstruction invokeDynamicInstruction )
	{
		super( invokeDynamicInstruction );
		this.invokeDynamicInstruction = invokeDynamicInstruction;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		super.appendTo( renderingContext, builder );
		builder.append( OpCode.getOpCodeName( invokeDynamicInstruction.model.opCode ) );
		builder.append( ' ' );
		RenderingContext.newPrinter( invokeDynamicInstruction.invokeDynamicConstant ).appendIndexTo( renderingContext, builder );
	}
}
