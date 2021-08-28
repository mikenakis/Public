package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.BootstrapMethodsAttribute;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.twig.Twig;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link BootstrapMethodsAttribute} {@link AttributePrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class BootstrapMethodsAttributePrinter extends AttributePrinter
{
	private final BootstrapMethodsAttribute bootstrapMethodsAttribute;

	public BootstrapMethodsAttributePrinter( BootstrapMethodsAttribute bootstrapMethodsAttribute )
	{
		this.bootstrapMethodsAttribute = bootstrapMethodsAttribute;
	}

	@Override public void appendGildedTo( RenderingContext renderingContext, StringBuilder builder )
	{
		bootstrapMethodsAttribute.toStringBuilder( builder );
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		builder.append( " (" );
		bootstrapMethodsAttribute.toStringBuilder( builder );
		builder.append( ')' );
	}

	@Override public List<Twig> getTwigChildren( RenderingContext renderingContext )
	{
		return bootstrapMethodsAttribute.entries.stream().map( m -> RenderingContext.newPrinter( m ).toTwig( renderingContext, "" ) ).collect( Collectors.toList() );
	}
}
