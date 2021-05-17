package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.code.OpCode;
import mikenakis.bytecode.attributes.code.instructions.OperandlessLoadConstantInstruction;
import mikenakis.bytecode.dumping.RenderingContext;

/**
 * {@link OperandlessLoadConstantInstruction} {@link InstructionPrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class OperandlessLoadConstantInstructionPrinter extends InstructionPrinter
{
	private final OperandlessLoadConstantInstruction operandlessLoadConstantInstruction;

	public OperandlessLoadConstantInstructionPrinter( OperandlessLoadConstantInstruction operandlessLoadConstantInstruction )
	{
		super( operandlessLoadConstantInstruction );
		this.operandlessLoadConstantInstruction = operandlessLoadConstantInstruction;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		super.appendTo( renderingContext, builder );
		builder.append( OpCode.getOpCodeName( operandlessLoadConstantInstruction.model.opCode ) );
	}
}
