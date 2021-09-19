package mikenakis.bytecode.model;

import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents an annotation parameter.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class AnnotationParameter // "element_value" in the jvms
{
	public static AnnotationParameter of( Mutf8Constant nameConstant, AnnotationValue annotationValue )
	{
		return new AnnotationParameter( nameConstant, annotationValue );
	}

	private final Mutf8Constant nameConstant;
	private final AnnotationValue annotationValue;

	private AnnotationParameter( Mutf8Constant nameConstant, AnnotationValue annotationValue )
	{
		this.nameConstant = nameConstant;
		this.annotationValue = annotationValue;
	}

	public Mutf8Constant nameConstant() { return nameConstant; }
	public AnnotationValue annotationValue() { return annotationValue; }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "name = " + nameConstant + ", value = " + annotationValue;
	}
}
