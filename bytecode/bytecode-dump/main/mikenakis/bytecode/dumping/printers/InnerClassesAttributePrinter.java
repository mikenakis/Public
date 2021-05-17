package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.InnerClassesAttribute;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.twig.Twig;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link InnerClassesAttribute} {@link AttributePrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class InnerClassesAttributePrinter extends AttributePrinter
{
	private final InnerClassesAttribute innerClassesAttribute;

	public InnerClassesAttributePrinter( InnerClassesAttribute innerClassesAttribute )
	{
		this.innerClassesAttribute = innerClassesAttribute;
	}

	@Override public void appendGildedTo( RenderingContext renderingContext, StringBuilder builder )
	{
		innerClassesAttribute.toStringBuilder( builder );
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		builder.append( " (" );
		innerClassesAttribute.toStringBuilder( builder );
		builder.append( ')' );
	}

	@Override public List<Twig> getTwigChildren( RenderingContext renderingContext )
	{
		return innerClassesAttribute.innerClasses.stream().map( c -> renderingContext.newPrinter( c ).toTwig( renderingContext, "" ) ).collect( Collectors.toList() );
	}
}
