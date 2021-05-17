package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.ParameterAnnotationsAttribute;
import mikenakis.bytecode.dumping.Printer;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.twig.Twig;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link ParameterAnnotationsAttribute.Entry} {@link Printer}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ParameterAnnotationsAttributeEntryPrinter extends Printer
{
	private final ParameterAnnotationsAttribute.Entry entry;

	public ParameterAnnotationsAttributeEntryPrinter( ParameterAnnotationsAttribute.Entry entry )
	{
		this.entry = entry;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		builder.append( "ParameterAnnotations " );
		entry.toStringBuilder( builder );
	}

	@Override public List<Twig> getTwigChildren( RenderingContext renderingContext )
	{
		return entry.annotations.stream().map( a -> renderingContext.newPrinter( a ).toTwig( renderingContext, "annotation" ) ).collect( Collectors.toList() );
	}
}
