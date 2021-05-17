package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.code.OpCode;
import mikenakis.bytecode.attributes.code.instructions.ImmediateLoadConstantInstruction;
import mikenakis.bytecode.dumping.RenderingContext;

/**
 * {@link ImmediateLoadConstantInstruction} {@link InstructionPrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ImmediateLoadConstantInstructionPrinter extends InstructionPrinter
{
	private final ImmediateLoadConstantInstruction immediateLoadConstantInstruction;

	public ImmediateLoadConstantInstructionPrinter( ImmediateLoadConstantInstruction immediateLoadConstantInstruction )
	{
		super( immediateLoadConstantInstruction );
		this.immediateLoadConstantInstruction = immediateLoadConstantInstruction;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		super.appendTo( renderingContext, builder );
		builder.append( OpCode.getOpCodeName( immediateLoadConstantInstruction.model.opCode ) );
		builder.append( ' ' );
		builder.append( immediateLoadConstantInstruction.immediateValue );
	}
}
