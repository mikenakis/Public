package mikenakis.bytecode.model.annotationvalues;

import mikenakis.bytecode.model.AnnotationValue;
import mikenakis.bytecode.model.ByteCodeAnnotation;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents an annotation {@link AnnotationValue}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class AnnotationAnnotationValue extends AnnotationValue
{
	public static final String NAME = "annotation";

	public static AnnotationAnnotationValue of( ByteCodeAnnotation annotation )
	{
		return new AnnotationAnnotationValue( annotation );
	}

	private final ByteCodeAnnotation annotation;

	private AnnotationAnnotationValue( ByteCodeAnnotation annotation )
	{
		super( AnnotationTag );
		this.annotation = annotation;
	}

	public ByteCodeAnnotation annotation()
	{
		return annotation;
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
