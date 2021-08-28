package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.code.OpCode;
import mikenakis.bytecode.attributes.code.RelativeInstructionReference;
import mikenakis.bytecode.attributes.code.instructions.TableSwitchInstruction;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.kit.Kit;

/**
 * {@link TableSwitchInstruction} {@link InstructionPrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class TableSwitchInstructionPrinter extends InstructionPrinter
{
	private final TableSwitchInstruction tableSwitchInstruction;

	public TableSwitchInstructionPrinter( TableSwitchInstruction tableSwitchInstruction )
	{
		super( tableSwitchInstruction );
		this.tableSwitchInstruction = tableSwitchInstruction;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		super.appendTo( renderingContext, builder );
		builder.append( OpCode.getOpCodeName( tableSwitchInstruction.model.opCode ) );
		builder.append( " default: " );
		RenderingContext.newPrinter( tableSwitchInstruction.defaultInstructionReference ).appendTo( renderingContext, builder );
		builder.append( " range: " ).append( tableSwitchInstruction.lowValue );
		builder.append( " - " ).append( tableSwitchInstruction.lowValue + tableSwitchInstruction.instructionReferences.size() - 1 );
		builder.append( " offsets: [" );
		boolean first = true;
		for( RelativeInstructionReference instructionReference : tableSwitchInstruction.instructionReferences )
		{
			first = Kit.stringBuilder.appendDelimiter( builder, first, ", " );
			RenderingContext.newPrinter( instructionReference ).appendTo( renderingContext, builder );
		}
		builder.append( ']' );
	}
}
