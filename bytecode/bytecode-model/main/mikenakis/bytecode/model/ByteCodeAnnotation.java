package mikenakis.bytecode.model;

import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.List;

/**
 * Represents an annotation.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ByteCodeAnnotation
{
	public static ByteCodeAnnotation of( Mutf8Constant typeConstant, List<AnnotationParameter> annotationParameters )
	{
		return new ByteCodeAnnotation( typeConstant, annotationParameters );
	}

	public final Mutf8Constant typeConstant;
	private final List<AnnotationParameter> annotationParameters;

	private ByteCodeAnnotation( Mutf8Constant typeConstant, List<AnnotationParameter> annotationParameters )
	{
		this.typeConstant = typeConstant;
		this.annotationParameters = annotationParameters;
	}

	public List<AnnotationParameter> annotationParameters() { return annotationParameters; }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "type = " + typeConstant + ", " + annotationParameters.size() + " parameters";
	}
}
