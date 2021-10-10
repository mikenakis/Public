package mikenakis.bytecode.exceptions;

import mikenakis.bytecode.model.annotationvalues.ConstAnnotationValue;
import mikenakis.kit.UncheckedException;

/**
 * "Invalid {@link ConstAnnotationValue} Tag" exception.
 *
 * @author michael.gr
 */
public final class InvalidConstAnnotationValueTagException extends UncheckedException
{
	public final char annotationValueTag;

	public InvalidConstAnnotationValueTagException( char annotationValueTag )
	{
		this.annotationValueTag = annotationValueTag;
	}
}
