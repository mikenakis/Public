package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.annotationvalues.ArrayAnnotationValue;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.twig.Twig;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link ArrayAnnotationValue} {@link AnnotationValuePrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ArrayAnnotationValuePrinter extends AnnotationValuePrinter
{
	private final ArrayAnnotationValue arrayAnnotationValue;

	public ArrayAnnotationValuePrinter( ArrayAnnotationValue arrayAnnotationValue )
	{
		super( arrayAnnotationValue );
		this.arrayAnnotationValue = arrayAnnotationValue;
	}

	@Override public void appendGildedTo( RenderingContext renderingContext, StringBuilder builder )
	{
		arrayAnnotationValue.toStringBuilder( builder );
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		builder.append( arrayAnnotationValue.annotationValues.size() );
		builder.append( " elements" );
	}

	@Override public List<Twig> getTwigChildren( RenderingContext renderingContext )
	{
		return arrayAnnotationValue.annotationValues.stream().map( a -> renderingContext.newPrinter( a ).toTwig( renderingContext, "element" ) ).collect( Collectors.toList() );
	}
}
