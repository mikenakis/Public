package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.code.OpCode;
import mikenakis.bytecode.attributes.code.instructions.OperandlessInstruction;
import mikenakis.bytecode.dumping.RenderingContext;

/**
 * {@link OperandlessInstruction} {@link InstructionPrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class OperandlessInstructionPrinter extends InstructionPrinter
{
	private final OperandlessInstruction operandlessInstruction;

	public OperandlessInstructionPrinter( OperandlessInstruction operandlessInstruction )
	{
		super( operandlessInstruction );
		this.operandlessInstruction = operandlessInstruction;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		super.appendTo( renderingContext, builder );
		builder.append( OpCode.getOpCodeName( operandlessInstruction.model.opCode ) );
	}
}
