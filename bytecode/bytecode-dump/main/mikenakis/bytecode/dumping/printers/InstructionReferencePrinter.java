package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.code.InstructionReference;
import mikenakis.bytecode.dumping.Printer;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.Style;

/**
 * {@link InstructionReference} {@link Printer}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class InstructionReferencePrinter extends Printer
{
	private final InstructionReference instructionReference;

	public InstructionReferencePrinter( InstructionReference instructionReference )
	{
		this.instructionReference = instructionReference;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		if( renderingContext.style.raw )
			instructionReference.toStringBuilder( builder );
		if( renderingContext.style == Style.MIXED )
			builder.append( ' ' );
		if( renderingContext.style.gild )
		{
			builder.append( RenderingContext.GILDING_PREFIX );
			appendGildedTo( renderingContext, builder );
			builder.append( RenderingContext.GILDING_SUFFIX );
		}
	}

	public void appendGildedTo( RenderingContext renderingContext, StringBuilder builder )
	{
		String label = renderingContext.getLabel( instructionReference.getTargetInstruction() );
		builder.append( label );
	}
}
