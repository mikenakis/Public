package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.LocalVariableTypeTableAttribute;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.twig.Twig;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link LocalVariableTypeTableAttribute} {@link AttributePrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class LocalVariableTypeTableAttributePrinter extends AttributePrinter
{
	private final LocalVariableTypeTableAttribute localVariableTypeTableAttribute;

	public LocalVariableTypeTableAttributePrinter( LocalVariableTypeTableAttribute localVariableTypeTableAttribute )
	{
		this.localVariableTypeTableAttribute = localVariableTypeTableAttribute;
	}

	@Override public void appendGildedTo( RenderingContext renderingContext, StringBuilder builder )
	{
		localVariableTypeTableAttribute.toStringBuilder( builder );
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		builder.append( " (" );
		localVariableTypeTableAttribute.toStringBuilder( builder );
		builder.append( ')' );
	}

	@Override public List<Twig> getTwigChildren( RenderingContext renderingContext )
	{
		return localVariableTypeTableAttribute.entries.stream().map( e -> RenderingContext.newPrinter( e ).toTwig( renderingContext, "" ) ).collect( Collectors.toList() );
	}
}
