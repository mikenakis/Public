package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.AnnotationParameter;
import mikenakis.bytecode.dumping.Printer;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.twig.Twig;

import java.util.List;

/**
 * {@link AnnotationParameter} {@link Printer}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class AnnotationParameterPrinter extends Printer
{
	private final AnnotationParameter annotationParameter;

	public AnnotationParameterPrinter( AnnotationParameter annotationParameter )
	{
		this.annotationParameter = annotationParameter;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		builder.append( "name = " );
		renderingContext.newPrinter( annotationParameter.nameConstant ).appendIndexTo( renderingContext, builder );
	}

	@Override public List<Twig> getTwigChildren( RenderingContext renderingContext )
	{
		return List.of( renderingContext.newPrinter( annotationParameter.annotationValue )
			.toTwig( renderingContext, annotationParameter.annotationValue.kind.name + " value = " ) );
	}
}
