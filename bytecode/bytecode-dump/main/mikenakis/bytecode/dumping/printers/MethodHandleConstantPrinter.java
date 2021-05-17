package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.constants.MethodHandleConstant;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.Style;

/**
 * {@link MethodHandleConstant} {@link ConstantPrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class MethodHandleConstantPrinter extends ConstantPrinter
{
	private final MethodHandleConstant methodHandleConstant;

	public MethodHandleConstantPrinter( MethodHandleConstant methodHandleConstant )
	{
		super( methodHandleConstant );
		this.methodHandleConstant = methodHandleConstant;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		if( renderingContext.style.raw )
		{
			builder.append( MethodHandleConstant.ReferenceKind.fromNumber( methodHandleConstant.referenceKind ).name() );
			builder.append( ' ' );
			renderingContext.newPrinter( methodHandleConstant.referenceConstant ).appendRawIndexTo( renderingContext, builder );
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
		builder.append( MethodHandleConstant.ReferenceKind.fromNumber( methodHandleConstant.referenceKind ).name() );
		builder.append( ' ' ); //", referenceConstant = " );
		renderingContext.newPrinter( methodHandleConstant.referenceConstant ).appendGildedTo( renderingContext, builder );
	}
}
