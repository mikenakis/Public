package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.TypeAnnotationsAttribute;
import mikenakis.bytecode.dumping.Printer;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.twig.Twig;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link TypeAnnotationsAttribute.TypePath} {@link Printer}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class TypeAnnotationsAttributeTypePathPrinter extends Printer
{
	public final TypeAnnotationsAttribute.TypePath typePath;

	public TypeAnnotationsAttributeTypePathPrinter( TypeAnnotationsAttribute.TypePath typePath )
	{
		this.typePath = typePath;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		typePath.toStringBuilder( builder );
	}

	@Override public List<Twig> getTwigChildren( RenderingContext renderingContext )
	{
		return typePath.entries.stream().map( e -> renderingContext.newPrinter( e ).toTwig( renderingContext, "" ) ).collect( Collectors.toList() );
	}
}
