package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.MethodParametersAttribute;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.twig.Twig;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link MethodParametersAttribute} {@link AttributePrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class MethodParametersAttributePrinter extends AttributePrinter
{
	private final MethodParametersAttribute methodParametersAttribute;

	public MethodParametersAttributePrinter( MethodParametersAttribute methodParametersAttribute )
	{
		this.methodParametersAttribute = methodParametersAttribute;
	}

	@Override public void appendGildedTo( RenderingContext renderingContext, StringBuilder builder )
	{
		methodParametersAttribute.toStringBuilder( builder );
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		builder.append( " (" );
		methodParametersAttribute.toStringBuilder( builder );
		builder.append( ')' );
	}

	@Override public List<Twig> getTwigChildren( RenderingContext renderingContext )
	{
		return methodParametersAttribute.entries.stream().map( p -> RenderingContext.newPrinter( p ).toTwig( renderingContext, "" ) ).collect( Collectors.toList() );
	}
}
