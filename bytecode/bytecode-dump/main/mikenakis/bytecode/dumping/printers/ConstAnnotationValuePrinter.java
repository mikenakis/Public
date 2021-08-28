package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.annotationvalues.ConstAnnotationValue;
import mikenakis.bytecode.dumping.RenderingContext;

/**
 * {@link ConstAnnotationValue} {@link AnnotationValuePrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ConstAnnotationValuePrinter extends AnnotationValuePrinter
{
	private final ConstAnnotationValue constAnnotationValue;

	public ConstAnnotationValuePrinter( ConstAnnotationValue constAnnotationValue )
	{
		super( constAnnotationValue );
		this.constAnnotationValue = constAnnotationValue;
	}

	@Override public void appendGildedTo( RenderingContext renderingContext, StringBuilder builder )
	{
		builder.append( constAnnotationValue.kind.name );
		builder.append( " value = " );
		RenderingContext.newPrinter( constAnnotationValue.valueConstant ).appendGildedTo( renderingContext, builder );
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		RenderingContext.newPrinter( constAnnotationValue.valueConstant ).appendIndexTo( renderingContext, builder );
	}
}
