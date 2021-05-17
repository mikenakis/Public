package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.ByteCodeHelpers;
import mikenakis.bytecode.Constant;
import mikenakis.bytecode.dumping.Printer;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.Style;

/**
 * {@link Constant} {@link Printer}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class ConstantPrinter extends Printer
{
	private final Constant constant;

	protected ConstantPrinter( Constant constant )
	{
		this.constant = constant;
	}

	public abstract void appendGildedTo( RenderingContext renderingContext, StringBuilder builder );

	public final void appendRawIndexTo( RenderingContext renderingContext, StringBuilder builder )
	{
		Integer index = renderingContext.byteCodeType.constantPool.getIndex( constant );
		builder.append( ByteCodeHelpers.getConstantId( index ) );
	}

	public final void appendIndexTo( RenderingContext renderingContext, StringBuilder builder )
	{
		if( renderingContext.style.raw )
			appendRawIndexTo( renderingContext, builder );
		if( renderingContext.style == Style.MIXED )
			builder.append( ' ' );
		if( renderingContext.style.gild )
		{
			builder.append( RenderingContext.GILDING_PREFIX );
			appendGildedTo( renderingContext, builder );
			builder.append( RenderingContext.GILDING_SUFFIX );
		}
	}
}
