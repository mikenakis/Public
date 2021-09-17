package mikenakis.bytecode.model;

import mikenakis.bytecode.model.constants.Utf8Constant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents an annotation parameter.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class AnnotationParameter // "element_value" in the jvms
{
	public static AnnotationParameter of( Utf8Constant nameConstant, AnnotationValue annotationValue )
	{
		return new AnnotationParameter( nameConstant, annotationValue );
	}

	private final Utf8Constant nameConstant;
	private final AnnotationValue annotationValue;

	private AnnotationParameter( Utf8Constant nameConstant, AnnotationValue annotationValue )
	{
		this.nameConstant = nameConstant;
		this.annotationValue = annotationValue;
	}

	public Utf8Constant nameConstant() { return nameConstant; }
	public AnnotationValue annotationValue() { return annotationValue; }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "name = " + nameConstant + ", value = " + annotationValue;
	}
}
