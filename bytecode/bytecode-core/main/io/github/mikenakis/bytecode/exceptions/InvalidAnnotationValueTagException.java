package io.github.mikenakis.bytecode.exceptions;

import io.github.mikenakis.bytecode.model.AnnotationValue;
import io.github.mikenakis.kit.exceptions.UncheckedException;

/**
 * "Invalid {@link AnnotationValue} Tag" exception.
 *
 * @author michael.gr
 */
public final class InvalidAnnotationValueTagException extends UncheckedException
{
	public final char annotationValueTag;

	public InvalidAnnotationValueTagException( char annotationValueTag )
	{
		this.annotationValueTag = annotationValueTag;
	}
}
