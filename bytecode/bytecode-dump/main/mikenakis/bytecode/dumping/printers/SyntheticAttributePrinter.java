package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.SyntheticAttribute;
import mikenakis.bytecode.dumping.RenderingContext;

/**
 * {@link SyntheticAttribute} {@link AttributePrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class SyntheticAttributePrinter extends AttributePrinter
{
	public final SyntheticAttribute syntheticAttribute;

	public SyntheticAttributePrinter( SyntheticAttribute syntheticAttribute )
	{
		this.syntheticAttribute = syntheticAttribute;
	}

	@Override public void appendGildedTo( RenderingContext renderingContext, StringBuilder builder )
	{
		/* nothing to do */
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		/* nothing to do */
	}
}
