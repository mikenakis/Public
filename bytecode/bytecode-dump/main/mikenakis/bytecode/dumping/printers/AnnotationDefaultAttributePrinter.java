package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.AnnotationDefaultAttribute;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.twig.Twig;

import java.util.List;

/**
 * {@link AnnotationDefaultAttribute} {@link AttributePrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class AnnotationDefaultAttributePrinter extends AttributePrinter
{
	private final AnnotationDefaultAttribute annotationDefaultAttribute;

	public AnnotationDefaultAttributePrinter( AnnotationDefaultAttribute annotationDefaultAttribute )
	{
		this.annotationDefaultAttribute = annotationDefaultAttribute;
	}

	@Override public void appendGildedTo( RenderingContext renderingContext, StringBuilder builder )
	{
		builder.append( "value = " );
		renderingContext.newPrinter( annotationDefaultAttribute.annotationValue ).appendGildedTo( renderingContext, builder );
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		/* nothing to do */
	}

	@Override public List<Twig> getTwigChildren( RenderingContext renderingContext )
	{
		return List.of( renderingContext.newPrinter( annotationDefaultAttribute.annotationValue )
			.toTwig( renderingContext, annotationDefaultAttribute.annotationValue.kind.name + " value = " ) );
	}
}
