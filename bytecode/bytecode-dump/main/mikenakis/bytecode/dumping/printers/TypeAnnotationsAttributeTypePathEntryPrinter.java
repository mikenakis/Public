package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.TypeAnnotationsAttribute;
import mikenakis.bytecode.dumping.Printer;
import mikenakis.bytecode.dumping.RenderingContext;

/**
 * {@link TypeAnnotationsAttribute.TypePath.Entry} {@link Printer}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class TypeAnnotationsAttributeTypePathEntryPrinter extends Printer
{
	public final TypeAnnotationsAttribute.TypePath.Entry entry;

	public TypeAnnotationsAttributeTypePathEntryPrinter( TypeAnnotationsAttribute.TypePath.Entry entry )
	{
		this.entry = entry;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		entry.toStringBuilder( builder );
	}
}
