package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.TypeAnnotationsAttribute;
import mikenakis.bytecode.dumping.Printer;
import mikenakis.bytecode.dumping.RenderingContext;

/**
 * {@link TypeAnnotationsAttribute.Target} {@link Printer}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class TypeAnnotationsAttributeTargetPrinter extends Printer
{
	public final TypeAnnotationsAttribute.Target target;

	public TypeAnnotationsAttributeTargetPrinter( TypeAnnotationsAttribute.Target target )
	{
		this.target = target;
	}

	@Override public final void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		target.toStringBuilder( builder );
	}
}
