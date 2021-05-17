package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.ByteCodeHelpers;
import mikenakis.bytecode.annotationvalues.EnumAnnotationValue;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.Style;

/**
 * {@link EnumAnnotationValue} {@link AnnotationValuePrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class EnumAnnotationValuePrinter extends AnnotationValuePrinter
{
	private final EnumAnnotationValue enumAnnotationValue;

	public EnumAnnotationValuePrinter( EnumAnnotationValue enumAnnotationValue )
	{
		super( enumAnnotationValue );
		this.enumAnnotationValue = enumAnnotationValue;
	}

	@Override public void appendGildedTo( RenderingContext renderingContext, StringBuilder builder )
	{
		enumAnnotationValue.toStringBuilder( builder );
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		if( renderingContext.style.raw )
		{
			builder.append( "type = " );
			renderingContext.newPrinter( enumAnnotationValue.typeNameConstant ).appendRawIndexTo( renderingContext, builder );
			builder.append( ", value = " );
			renderingContext.newPrinter( enumAnnotationValue.valueNameConstant ).appendRawIndexTo( renderingContext, builder );
		}
		if( renderingContext.style == Style.MIXED )
			builder.append( ' ' );
		if( renderingContext.style.gild )
		{
			builder.append( RenderingContext.GILDING_PREFIX );
			builder.append( ByteCodeHelpers.getJavaTypeNameFromDescriptorTypeName( enumAnnotationValue.typeNameConstant.getStringValue() ) );
			builder.append( '.' );
			builder.append( enumAnnotationValue.valueNameConstant.getStringValue() );
			builder.append( RenderingContext.GILDING_SUFFIX );
		}
	}
}
