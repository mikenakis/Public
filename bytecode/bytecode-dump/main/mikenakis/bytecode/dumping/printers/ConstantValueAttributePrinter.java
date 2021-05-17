package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.ConstantValueAttribute;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.Style;

/**
 * {@link ConstantValueAttribute} {@link AttributePrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ConstantValueAttributePrinter extends AttributePrinter
{
	private final ConstantValueAttribute constantValueAttribute;

	public ConstantValueAttributePrinter( ConstantValueAttribute constantValueAttribute )
	{
		this.constantValueAttribute = constantValueAttribute;
	}

	@Override public void appendGildedTo( RenderingContext renderingContext, StringBuilder builder )
	{
		builder.append( "value = " );
		renderingContext.newPrinter( constantValueAttribute.valueConstant ).appendGildedTo( renderingContext, builder );
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		if( renderingContext.style.raw )
			renderingContext.newPrinter( constantValueAttribute.valueConstant ).appendRawIndexTo( renderingContext, builder );
		if( renderingContext.style == Style.MIXED )
			builder.append( ' ' );
		if( renderingContext.style.gild )
		{
			builder.append( RenderingContext.GILDING_PREFIX );
			renderingContext.newPrinter( constantValueAttribute.valueConstant ).appendGildedTo( renderingContext, builder );
			builder.append( RenderingContext.GILDING_SUFFIX );
		}
	}
}
