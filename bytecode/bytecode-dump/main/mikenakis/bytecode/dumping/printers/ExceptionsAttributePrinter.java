package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.ExceptionsAttribute;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.twig.Twig;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link ExceptionsAttribute} {@link AttributePrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ExceptionsAttributePrinter extends AttributePrinter
{
	private final ExceptionsAttribute exceptionsAttribute;

	public ExceptionsAttributePrinter( ExceptionsAttribute exceptionsAttribute )
	{
		this.exceptionsAttribute = exceptionsAttribute;
	}

	@Override public void appendGildedTo( RenderingContext renderingContext, StringBuilder builder )
	{
		exceptionsAttribute.toStringBuilder( builder );
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		builder.append( " (" );
		exceptionsAttribute.toStringBuilder( builder );
		builder.append( ')' );
	}

	@Override public List<Twig> getTwigChildren( RenderingContext renderingContext )
	{
		return exceptionsAttribute.exceptionClassConstants.stream().map( ci -> RenderingContext.newPrinter( ci ).toTwig( renderingContext, "exception " ) ).collect(
			Collectors.toList() );
	}
}
