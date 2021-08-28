package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.Constant;
import mikenakis.bytecode.attributes.BootstrapMethod;
import mikenakis.bytecode.dumping.Printer;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.kit.Kit;

/**
 * {@link BootstrapMethod} {@link Printer}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class BootstrapMethodPrinter extends Printer
{
	private final BootstrapMethod bootstrapMethod;

	public BootstrapMethodPrinter( BootstrapMethod bootstrapMethod )
	{
		this.bootstrapMethod = bootstrapMethod;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		if( renderingContext.style.raw )
		{
			builder.append( " methodHandle = " );
			RenderingContext.newPrinter( bootstrapMethod.methodHandleConstant ).appendRawIndexTo( renderingContext, builder );
			if( !bootstrapMethod.argumentConstants.isEmpty() )
			{
				builder.append( " arguments: " );
				boolean first = true;
				for( Constant argumentConstant : bootstrapMethod.argumentConstants )
				{
					first = Kit.stringBuilder.appendDelimiter( builder, first, ", " );
					RenderingContext.newPrinter( argumentConstant ).appendRawIndexTo( renderingContext, builder );
				}
			}
		}
		if( renderingContext.style.gild )
		{
			builder.append( ' ' );
			appendGildedTo( renderingContext, builder );
		}
	}

	public void appendGildedTo( RenderingContext renderingContext, StringBuilder builder )
	{
		RenderingContext.newPrinter( bootstrapMethod.methodHandleConstant ).appendGildedTo( renderingContext, builder );
		if( !bootstrapMethod.argumentConstants.isEmpty() )
		{
			builder.append( ' ' );
			boolean first = true;
			for( Constant argumentConstant : bootstrapMethod.argumentConstants )
			{
				first = Kit.stringBuilder.appendDelimiter( builder, first, ", " );
				RenderingContext.newPrinter( argumentConstant ).appendGildedTo( renderingContext, builder );
			}
		}
	}
}
