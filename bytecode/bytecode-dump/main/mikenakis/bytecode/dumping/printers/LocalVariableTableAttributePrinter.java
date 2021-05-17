package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.LocalVariableTableAttribute;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.twig.Twig;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link LocalVariableTableAttribute} {@link AttributePrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class LocalVariableTableAttributePrinter extends AttributePrinter
{
	private final LocalVariableTableAttribute localVariableTableAttribute;

	public LocalVariableTableAttributePrinter( LocalVariableTableAttribute localVariableTableAttribute )
	{
		this.localVariableTableAttribute = localVariableTableAttribute;
	}

	@Override public void appendGildedTo( RenderingContext renderingContext, StringBuilder builder )
	{
		localVariableTableAttribute.toStringBuilder( builder );
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		builder.append( " (" );
		localVariableTableAttribute.toStringBuilder( builder );
		builder.append( ')' );
	}

	@Override public List<Twig> getTwigChildren( RenderingContext renderingContext )
	{
		return localVariableTableAttribute.localVariables.stream().map( e -> renderingContext.newPrinter( e ).toTwig( renderingContext, "" ) ).collect( Collectors.toList() );
	}
}
