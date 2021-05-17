package mikenakis.bytecode.dumping;

import mikenakis.bytecode.dumping.twig.Twig;

import java.util.List;

/**
 * Something that can be converted to a string.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class Printer
{
	public abstract void appendTo( RenderingContext renderingContext, StringBuilder builder );

	public final Twig toTwig( RenderingContext renderingContext, String prefix )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		appendTo( renderingContext, builder );
		return Twig.of( builder.toString(), getTwigChildren( renderingContext ) );
	}

	public List<Twig> getTwigChildren( RenderingContext renderingContext )
	{
		return List.of();
	}
}
