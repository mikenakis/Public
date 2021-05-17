package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.DeprecatedAttribute;
import mikenakis.bytecode.dumping.RenderingContext;

/**
 * {@link DeprecatedAttribute} {@link AttributePrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class DeprecatedAttributePrinter extends AttributePrinter
{
	public final DeprecatedAttribute deprecatedAttribute;

	public DeprecatedAttributePrinter( DeprecatedAttribute deprecatedAttribute )
	{
		this.deprecatedAttribute = deprecatedAttribute;
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
