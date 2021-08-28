package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.LineNumber;
import mikenakis.bytecode.dumping.Printer;
import mikenakis.bytecode.dumping.RenderingContext;

/**
 * {@link LineNumber} {@link Printer}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class LineNumberPrinter extends Printer
{
	private final LineNumber lineNumber;

	public LineNumberPrinter( LineNumber lineNumber )
	{
		this.lineNumber = lineNumber;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		builder.append( "lineNumber = " ).append( lineNumber ).append( ", startPc = " );
		RenderingContext.newPrinter( lineNumber.startPc ).appendTo( renderingContext, builder );
	}
}
