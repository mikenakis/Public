package mikenakis.bytecode.model;

import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents the "element_value_pair" of JVMS 4.7.16.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class AnnotationParameter
{
	public static AnnotationParameter of( Mutf8Constant nameConstant, AnnotationValue annotationValue )
	{
		return new AnnotationParameter( nameConstant, annotationValue );
	}

	public final Mutf8Constant nameConstant;
	public final AnnotationValue value;

	private AnnotationParameter( Mutf8Constant nameConstant, AnnotationValue value )
	{
		this.nameConstant = nameConstant;
		this.value = value;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "name = " + nameConstant + ", value = " + value;
	}
}
