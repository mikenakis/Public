package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.ParameterAnnotationsAttribute;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.twig.Twig;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link ParameterAnnotationsAttribute} {@link AttributePrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ParameterAnnotationsAttributePrinter extends AttributePrinter
{
	private final ParameterAnnotationsAttribute parameterAnnotationsAttribute;

	public ParameterAnnotationsAttributePrinter( ParameterAnnotationsAttribute parameterAnnotationsAttribute )
	{
		this.parameterAnnotationsAttribute = parameterAnnotationsAttribute;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		builder.append( " (" );
		parameterAnnotationsAttribute.toStringBuilder( builder );
		builder.append( ')' );
	}

	@Override public List<Twig> getTwigChildren( RenderingContext renderingContext )
	{
		return parameterAnnotationsAttribute.entries.stream().map( a -> renderingContext.newPrinter( a ).toTwig( renderingContext, "" ) ).collect( Collectors.toList() );
	}

	@Override public void appendGildedTo( RenderingContext renderingContext, StringBuilder builder )
	{
		parameterAnnotationsAttribute.toStringBuilder( builder );
	}
}
