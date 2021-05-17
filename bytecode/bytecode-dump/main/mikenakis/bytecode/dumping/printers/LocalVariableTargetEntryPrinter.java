package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.TypeAnnotationsAttribute;
import mikenakis.bytecode.dumping.Printer;
import mikenakis.bytecode.dumping.RenderingContext;

/**
 * {@link TypeAnnotationsAttribute.LocalVariableTarget.Entry} {@link Printer}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class LocalVariableTargetEntryPrinter extends Printer
{
	public final TypeAnnotationsAttribute.LocalVariableTarget.Entry entry;

	public LocalVariableTargetEntryPrinter( TypeAnnotationsAttribute.LocalVariableTarget.Entry entry )
	{
		this.entry = entry;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		entry.toStringBuilder( builder );
	}
}
