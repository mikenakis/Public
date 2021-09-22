package mikenakis.bytecode.model.annotationvalues;

import mikenakis.bytecode.model.AnnotationValue;
import mikenakis.bytecode.model.Annotation;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents an annotation {@link AnnotationValue}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class AnnotationAnnotationValue extends AnnotationValue
{
	public static final String NAME = "annotation";

	public static AnnotationAnnotationValue of( Annotation annotation )
	{
		return new AnnotationAnnotationValue( annotation );
	}

	public final Annotation annotation;

	private AnnotationAnnotationValue( Annotation annotation )
	{
		super( tagAnnotation );
		this.annotation = annotation;
	}

	@Deprecated @Override public AnnotationAnnotationValue asAnnotationAnnotationValue()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "annotation = { " + annotation + " }";
	}
}
