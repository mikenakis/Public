package mikenakis.bytecode.exceptions;

import mikenakis.bytecode.model.AnnotationValue;
import mikenakis.kit.UncheckedException;

/**
 * "Invalid {@link AnnotationValue} Tag" exception.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class InvalidAnnotationValueTagException extends UncheckedException
{
	public final char annotationValueTag;

	public InvalidAnnotationValueTagException( char annotationValueTag )
	{
		this.annotationValueTag = annotationValueTag;
	}
}
