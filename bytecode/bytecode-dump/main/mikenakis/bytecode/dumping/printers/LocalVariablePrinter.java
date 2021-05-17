package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.LocalVariable;
import mikenakis.bytecode.dumping.Printer;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.Style;

/**
 * {@link LocalVariable} {@link Printer}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class LocalVariablePrinter extends Printer
{
	private final LocalVariable localVariable;

	public LocalVariablePrinter( LocalVariable localVariable )
	{
		this.localVariable = localVariable;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		builder.append( "index = " ).append( localVariable.index );
		builder.append( ", startPc = " );
		renderingContext.newPrinter( localVariable.startInstructionReference ).appendTo( renderingContext, builder );
		if( renderingContext.style.raw )
			builder.append( ", length = " ).append( localVariable.getLength() );
		if( renderingContext.style == Style.MIXED )
			builder.append( ' ' );
		if( renderingContext.style.gild )
		{
			builder.append( RenderingContext.GILDING_PREFIX );
			builder.append( "endPc = " );
			renderingContext.newPrinter( localVariable.endInstructionReference ).appendGildedTo( renderingContext, builder );
			builder.append( RenderingContext.GILDING_SUFFIX );
		}
		renderingContext.appendNameAndDescriptor( builder, localVariable.nameConstant, localVariable.descriptorConstant );
	}
}
