package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.SourceFileAttribute;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.Style;

/**
 * {@link SourceFileAttribute} {@link AttributePrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class SourceFileAttributePrinter extends AttributePrinter
{
	private final SourceFileAttribute sourceFileAttribute;

	public SourceFileAttributePrinter( SourceFileAttribute sourceFileAttribute )
	{
		this.sourceFileAttribute = sourceFileAttribute;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		if( renderingContext.style.raw )
		{
			builder.append( "value = " );
			RenderingContext.newPrinter( sourceFileAttribute.valueConstant ).appendRawIndexTo( renderingContext, builder );
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
		RenderingContext.newPrinter( sourceFileAttribute.valueConstant ).appendGildedTo( renderingContext, builder );
	}
}
