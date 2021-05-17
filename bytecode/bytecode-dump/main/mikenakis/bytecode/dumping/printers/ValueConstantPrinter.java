package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.constants.ValueConstant;
import mikenakis.bytecode.dumping.RenderingContext;

/**
 * {@link ValueConstant} {@link ConstantPrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ValueConstantPrinter extends ConstantPrinter
{
	private final ValueConstant<?> valueConstant;

	public ValueConstantPrinter( ValueConstant<?> valueConstant )
	{
		super( valueConstant );
		this.valueConstant = valueConstant;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		appendGildedTo( renderingContext, builder );
	}

	@Override public void appendGildedTo( RenderingContext renderingContext, StringBuilder builder )
	{
		valueConstant.toStringBuilder( builder );
	}
}
