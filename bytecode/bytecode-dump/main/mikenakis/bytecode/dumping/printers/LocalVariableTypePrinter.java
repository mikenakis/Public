package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.LocalVariableType;
import mikenakis.bytecode.dumping.Printer;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.Style;

/**
 * {@link LocalVariableType} {@link Printer}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class LocalVariableTypePrinter extends Printer
{
	private final LocalVariableType localVariableType;

	public LocalVariableTypePrinter( LocalVariableType localVariableType )
	{
		this.localVariableType = localVariableType;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		builder.append( "index = " ).append( localVariableType.index );
		builder.append( ", startPc = " );
		renderingContext.newPrinter( localVariableType.startPc ).appendTo( renderingContext, builder );
		builder.append( ", length = " ).append( localVariableType.length );
		if( renderingContext.style.raw )
		{
			builder.append( ", name = " );
			renderingContext.newPrinter( localVariableType.nameConstant ).appendRawIndexTo( renderingContext, builder );
			builder.append( ", signature = " );
			renderingContext.newPrinter( localVariableType.signatureConstant ).appendRawIndexTo( renderingContext, builder );
		}
		if( renderingContext.style == Style.MIXED )
			builder.append( ' ' );
		if( renderingContext.style.gild )
		{
			builder.append( RenderingContext.GILDING_PREFIX );
			RenderingContext.appendGildedNameAndSignature( builder, localVariableType.nameConstant, localVariableType.signatureConstant );
			builder.append( RenderingContext.GILDING_SUFFIX );
		}
	}
}
