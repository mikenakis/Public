package mikenakis.bytecode.model.annotationvalues;

import mikenakis.bytecode.model.ElementValue;
import mikenakis.bytecode.model.Annotation;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents an annotation {@link ElementValue}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class AnnotationElementValue extends ElementValue
{
	public static final String NAME = "annotation";

	public static AnnotationElementValue of( Annotation annotation )
	{
		return new AnnotationElementValue( annotation );
	}

	private final Annotation annotation;

	private AnnotationElementValue( Annotation annotation )
	{
		super( Tag.Annotation );
		this.annotation = annotation;
	}

	public Annotation annotation()
	{
		return annotation;
	}

	@Deprecated @Override public AnnotationElementValue asAnnotationAnnotationValue()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "annotation = { " + annotation + " }";
	}
}
