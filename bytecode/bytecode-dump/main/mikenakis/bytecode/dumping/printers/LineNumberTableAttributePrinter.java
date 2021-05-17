package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.LineNumberTableAttribute;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.twig.Twig;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link LineNumberTableAttribute} {@link AttributePrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class LineNumberTableAttributePrinter extends AttributePrinter
{
	private final LineNumberTableAttribute lineNumberTableAttribute;

	public LineNumberTableAttributePrinter( LineNumberTableAttribute lineNumberTableAttribute )
	{
		this.lineNumberTableAttribute = lineNumberTableAttribute;
	}

	@Override public void appendGildedTo( RenderingContext renderingContext, StringBuilder builder )
	{
		lineNumberTableAttribute.toStringBuilder( builder );
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		builder.append( " (" );
		lineNumberTableAttribute.toStringBuilder( builder );
		builder.append( ')' );
	}

	@Override public List<Twig> getTwigChildren( RenderingContext renderingContext )
	{
		return lineNumberTableAttribute.entries.stream().map( n -> renderingContext.newPrinter( n ).toTwig( renderingContext, "" ) ).collect( Collectors.toList() );
	}
}
