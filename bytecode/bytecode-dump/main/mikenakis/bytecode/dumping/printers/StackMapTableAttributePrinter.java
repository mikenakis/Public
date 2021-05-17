package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.StackMapTableAttribute;
import mikenakis.bytecode.attributes.stackmap.Frame;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.twig.Twig;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * {@link StackMapTableAttribute} {@link AttributePrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class StackMapTableAttributePrinter extends AttributePrinter
{
	private final StackMapTableAttribute stackMapTableAttribute;

	public StackMapTableAttributePrinter( StackMapTableAttribute stackMapTableAttribute )
	{
		this.stackMapTableAttribute = stackMapTableAttribute;
	}

	@Override public void appendGildedTo( RenderingContext renderingContext, StringBuilder builder )
	{
		stackMapTableAttribute.toStringBuilder( builder );
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		builder.append( " (" );
		stackMapTableAttribute.toStringBuilder( builder );
		builder.append( ')' );
	}

	@Override public List<Twig> getTwigChildren( RenderingContext renderingContext )
	{
		List<Twig> twigs = new ArrayList<>();
		Optional<Frame> previousFrame = Optional.empty();
		for( Frame frame : stackMapTableAttribute.frames )
		{
			Twig twig = renderingContext.newPrinter( frame, previousFrame ).toTwig( renderingContext, "" );
			twigs.add( twig );
			previousFrame = Optional.of( frame );
		}
		return twigs;
	}
}
