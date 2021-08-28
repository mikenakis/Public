package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.constants.StringConstant;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.Style;

/**
 * {@link StringConstant} {@link ConstantPrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class StringConstantPrinter extends ConstantPrinter
{
	private final StringConstant stringConstant;

	public StringConstantPrinter( StringConstant stringConstant )
	{
		super( stringConstant );
		this.stringConstant = stringConstant;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		if( renderingContext.style.raw )
			RenderingContext.newPrinter( stringConstant.valueUtf8Constant ).appendRawIndexTo( renderingContext, builder );
		if( renderingContext.style == Style.MIXED )
			builder.append( ' ' );
		if( renderingContext.style.gild )
		{
			builder.append( RenderingContext.GILDING_PREFIX );
			appendGildedTo( renderingContext, builder );
			builder.append( RenderingContext.GILDING_SUFFIX );
		}
	}

	@Override public void appendGildedTo( RenderingContext renderingContext, StringBuilder builder )
	{
		RenderingContext.newPrinter( stringConstant.valueUtf8Constant ).appendGildedTo( renderingContext, builder );
	}
}
