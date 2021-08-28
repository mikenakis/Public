package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.Attribute;
import mikenakis.bytecode.Attributes;
import mikenakis.bytecode.dumping.Printer;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.twig.Twig;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link Attributes} {@link Printer}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class AttributesPrinter extends Printer
{
	private final Attributes attributes;

	public AttributesPrinter( Attributes attributes )
	{
		this.attributes = attributes;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		builder.append( " (" );
		attributes.toStringBuilder( builder );
		builder.append( ")" );
	}

	@Override public List<Twig> getTwigChildren( RenderingContext renderingContext )
	{
		List<Twig> children = new ArrayList<>();
		for( Attribute attribute : attributes )
		{
			Twig twig = RenderingContext.newPrinter( attribute ).toTwig( renderingContext, attribute.name + ' ' );
			children.add( twig );
		}
		return children;
	}
}
