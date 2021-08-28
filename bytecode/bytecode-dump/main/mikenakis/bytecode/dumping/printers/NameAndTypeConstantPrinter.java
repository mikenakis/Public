package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.constants.NameAndTypeConstant;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.Style;

/**
 * {@link NameAndTypeConstant} {@link ConstantPrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class NameAndTypeConstantPrinter extends ConstantPrinter
{
	private final NameAndTypeConstant nameAndTypeConstant;

	public NameAndTypeConstantPrinter( NameAndTypeConstant nameAndTypeConstant )
	{
		super( nameAndTypeConstant );
		this.nameAndTypeConstant = nameAndTypeConstant;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		if( renderingContext.style.raw )
		{
			builder.append( "name = " );
			RenderingContext.newPrinter( nameAndTypeConstant.nameConstant ).appendRawIndexTo( renderingContext, builder );
			builder.append( ", descriptor = " );
			RenderingContext.newPrinter( nameAndTypeConstant.descriptorConstant ).appendRawIndexTo( renderingContext, builder );
		}
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
		RenderingContext.appendGildedNameAndTypeAndDescriptor( builder, nameAndTypeConstant.nameConstant, nameAndTypeConstant.descriptorConstant );
	}
}
