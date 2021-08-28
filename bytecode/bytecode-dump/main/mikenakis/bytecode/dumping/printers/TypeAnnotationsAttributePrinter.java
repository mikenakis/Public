package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.TypeAnnotationsAttribute;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.twig.Twig;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link TypeAnnotationsAttribute} {@link AttributePrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class TypeAnnotationsAttributePrinter extends AttributePrinter
{
	private final TypeAnnotationsAttribute typeAnnotationsAttribute;

	public TypeAnnotationsAttributePrinter( TypeAnnotationsAttribute typeAnnotationsAttribute )
	{
		this.typeAnnotationsAttribute = typeAnnotationsAttribute;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		builder.append( " (" );
		typeAnnotationsAttribute.toStringBuilder( builder );
		builder.append( ')' );
	}

	@Override public List<Twig> getTwigChildren( RenderingContext renderingContext )
	{
		return typeAnnotationsAttribute.entries.stream().map( a -> RenderingContext.newPrinter( a ).toTwig( renderingContext, "" ) ).collect( Collectors.toList() );
	}

	@Override public void appendGildedTo( RenderingContext renderingContext, StringBuilder builder )
	{
		typeAnnotationsAttribute.toStringBuilder( builder );
	}
}
