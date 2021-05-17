package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.UnknownAttribute;
import mikenakis.bytecode.dumping.RenderingContext;

/**
 * {@link UnknownAttribute} {@link AttributePrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class UnknownAttributePrinter extends AttributePrinter
{
	private final UnknownAttribute unknownAttribute;

	public UnknownAttributePrinter( UnknownAttribute unknownAttribute )
	{
		this.unknownAttribute = unknownAttribute;
	}

	@Override public void appendGildedTo( RenderingContext renderingContext, StringBuilder builder )
	{
		unknownAttribute.toStringBuilder( builder );
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		builder.append( unknownAttribute.buffer.getLength() ).append( " bytes" );
	}
}
