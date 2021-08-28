package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.annotationvalues.ClassAnnotationValue;
import mikenakis.bytecode.dumping.RenderingContext;

/**
 * {@link ClassAnnotationValue} {@link AnnotationValuePrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ClassAnnotationValuePrinter extends AnnotationValuePrinter
{
	private final ClassAnnotationValue classAnnotationValue;

	public ClassAnnotationValuePrinter( ClassAnnotationValue classAnnotationValue )
	{
		super( classAnnotationValue );
		this.classAnnotationValue = classAnnotationValue;
	}

	@Override public void appendGildedTo( RenderingContext renderingContext, StringBuilder builder )
	{
		classAnnotationValue.toStringBuilder( builder );
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		RenderingContext.newPrinter( classAnnotationValue.classConstant ).appendIndexTo( renderingContext, builder );
	}
}
