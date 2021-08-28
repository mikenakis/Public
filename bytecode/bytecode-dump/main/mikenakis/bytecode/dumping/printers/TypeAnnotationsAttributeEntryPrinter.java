package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.TypeAnnotationsAttribute;
import mikenakis.bytecode.dumping.Printer;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.twig.Twig;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link TypeAnnotationsAttribute.Entry} {@link Printer}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class TypeAnnotationsAttributeEntryPrinter extends Printer
{
	private final TypeAnnotationsAttribute.Entry entry;

	public TypeAnnotationsAttributeEntryPrinter( TypeAnnotationsAttribute.Entry entry )
	{
		this.entry = entry;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		entry.toStringBuilder( builder );
	}

	@Override public List<Twig> getTwigChildren( RenderingContext renderingContext )
	{
		return List.of(
			Twig.of( "target", RenderingContext.newPrinter( entry.target ).toTwig( renderingContext, "" ) ),
			Twig.of( "elementValuePairs",
				entry.elementValuePairs.stream().map( a -> RenderingContext.newPrinter( a ).toTwig( renderingContext, "" ) ).collect( Collectors.toList() ) ) );
	}
}
