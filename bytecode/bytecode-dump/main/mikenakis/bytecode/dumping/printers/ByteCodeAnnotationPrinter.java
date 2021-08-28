package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.ByteCodeAnnotation;
import mikenakis.bytecode.dumping.Printer;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.Style;
import mikenakis.bytecode.dumping.twig.Twig;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link ByteCodeAnnotation} {@link Printer}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ByteCodeAnnotationPrinter extends Printer
{
	private final ByteCodeAnnotation byteCodeAnnotation;

	public ByteCodeAnnotationPrinter( ByteCodeAnnotation byteCodeAnnotation )
	{
		this.byteCodeAnnotation = byteCodeAnnotation;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		builder.append( "name = " );
		if( renderingContext.style.raw )
		{
			RenderingContext.newPrinter( byteCodeAnnotation.nameConstant ).appendRawIndexTo( renderingContext, builder );
		}
		if( renderingContext.style == Style.MIXED )
			builder.append( ' ' );
		if( renderingContext.style.gild )
		{
			builder.append( RenderingContext.GILDING_PREFIX );
			builder.append( byteCodeAnnotation.getName() );
			builder.append( RenderingContext.GILDING_SUFFIX );
			builder.append( ' ' );
		}
		builder.append( ", " ).append( byteCodeAnnotation.getAnnotationParameters().size() ).append( " parameters" );
	}

	@Override public List<Twig> getTwigChildren( RenderingContext renderingContext )
	{
		return byteCodeAnnotation.getAnnotationParameters().stream().map( p -> RenderingContext.newPrinter( p ).toTwig( renderingContext, "parameter " ) ).collect(
			Collectors.toList() );
	}
}
