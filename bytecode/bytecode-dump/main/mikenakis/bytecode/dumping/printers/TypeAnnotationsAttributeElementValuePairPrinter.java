package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.TypeAnnotationsAttribute;
import mikenakis.bytecode.dumping.Printer;
import mikenakis.bytecode.dumping.RenderingContext;

/**
 * {@link TypeAnnotationsAttribute.ElementValuePair} {@link Printer}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class TypeAnnotationsAttributeElementValuePairPrinter extends Printer
{
	public final TypeAnnotationsAttribute.ElementValuePair elementValuePair;

	public TypeAnnotationsAttributeElementValuePairPrinter( TypeAnnotationsAttribute.ElementValuePair elementValuePair )
	{
		this.elementValuePair = elementValuePair;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		elementValuePair.toStringBuilder( builder );
	}
}
