package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.code.instructions.LookupSwitchInstruction;
import mikenakis.bytecode.dumping.Printer;
import mikenakis.bytecode.dumping.RenderingContext;

/**
 * {@link LookupSwitchInstruction.Entry} {@link Printer}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class LookupSwitchInstructionEntryPrinter extends Printer
{
	private final LookupSwitchInstruction.Entry entry;

	public LookupSwitchInstructionEntryPrinter( LookupSwitchInstruction.Entry entry )
	{
		this.entry = entry;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		builder.append( entry.value ).append( ":" );
		RenderingContext.newPrinter( entry.instructionReference ).appendTo( renderingContext, builder );
	}
}
