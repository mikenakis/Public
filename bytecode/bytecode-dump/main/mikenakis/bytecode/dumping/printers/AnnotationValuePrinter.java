package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.AnnotationValue;
import mikenakis.bytecode.dumping.Printer;
import mikenakis.bytecode.dumping.RenderingContext;

/**
 * {@link AnnotationValue} {@link Printer}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class AnnotationValuePrinter extends Printer
{
	private final AnnotationValue annotationValue;

	protected AnnotationValuePrinter( AnnotationValue annotationValue )
	{
		this.annotationValue = annotationValue;
	}

	public abstract void appendGildedTo( RenderingContext renderingContext, StringBuilder builder );

	public final void appendTo( StringBuilder builder )
	{
		annotationValue.toStringBuilder( builder );
	}
}
