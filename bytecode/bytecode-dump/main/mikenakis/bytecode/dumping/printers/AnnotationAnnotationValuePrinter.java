package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.annotationvalues.AnnotationAnnotationValue;
import mikenakis.bytecode.dumping.RenderingContext;

/**
 * {@link AnnotationAnnotationValue} {@link AnnotationValuePrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class AnnotationAnnotationValuePrinter extends AnnotationValuePrinter
{
	private final AnnotationAnnotationValue annotationAnnotationValue;

	public AnnotationAnnotationValuePrinter( AnnotationAnnotationValue annotationAnnotationValue )
	{
		super( annotationAnnotationValue );
		this.annotationAnnotationValue = annotationAnnotationValue;
	}

	@Override public void appendGildedTo( RenderingContext renderingContext, StringBuilder builder )
	{
		annotationAnnotationValue.toStringBuilder( builder );
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		RenderingContext.newPrinter( annotationAnnotationValue.annotation ).appendTo( renderingContext, builder );
	}
}
