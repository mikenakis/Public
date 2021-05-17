package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.AnnotationsAttribute;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.twig.Twig;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link AnnotationsAttribute} {@link AttributePrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class AnnotationsAttributePrinter extends AttributePrinter
{
	private final AnnotationsAttribute annotationsAttribute;

	public AnnotationsAttributePrinter( AnnotationsAttribute annotationsAttribute )
	{
		this.annotationsAttribute = annotationsAttribute;
	}

	@Override public void appendGildedTo( RenderingContext renderingContext, StringBuilder builder )
	{
		annotationsAttribute.toStringBuilder( builder );
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		builder.append( " (" );
		annotationsAttribute.toStringBuilder( builder );
		builder.append( ')' );
	}

	@Override public List<Twig> getTwigChildren( RenderingContext renderingContext )
	{
		return annotationsAttribute.getAnnotations().stream().map( a -> renderingContext.newPrinter( a ).toTwig( renderingContext, "annotation " ) ).collect( Collectors.toList() );
	}
}
