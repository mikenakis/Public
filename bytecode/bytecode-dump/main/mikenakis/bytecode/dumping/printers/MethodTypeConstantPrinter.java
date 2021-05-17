package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.constants.MethodTypeConstant;
import mikenakis.bytecode.dumping.RenderingContext;

/**
 * {@link MethodTypeConstant} {@link ConstantPrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class MethodTypeConstantPrinter extends ConstantPrinter
{
	private final MethodTypeConstant methodTypeConstant;

	public MethodTypeConstantPrinter( MethodTypeConstant methodTypeConstant )
	{
		super( methodTypeConstant );
		this.methodTypeConstant = methodTypeConstant;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		builder.append( "descriptor = " );
		renderingContext.newPrinter( methodTypeConstant.descriptorConstant ).appendIndexTo( renderingContext, builder );
	}

	@Override public void appendGildedTo( RenderingContext renderingContext, StringBuilder builder )
	{
		renderingContext.newPrinter( methodTypeConstant.descriptorConstant ).appendGildedTo( renderingContext, builder );
	}
}
