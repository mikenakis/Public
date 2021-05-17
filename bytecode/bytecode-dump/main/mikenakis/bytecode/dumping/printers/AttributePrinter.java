package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.Attribute;
import mikenakis.bytecode.dumping.Printer;
import mikenakis.bytecode.dumping.RenderingContext;

/**
 * {@link Attribute} {@link Printer}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class AttributePrinter extends Printer
{
	public abstract void appendGildedTo( RenderingContext renderingContext, StringBuilder builder );
}
