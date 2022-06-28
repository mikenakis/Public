package io.github.mikenakis.bytecode.exceptions;

import io.github.mikenakis.bytecode.model.annotationvalues.ConstAnnotationValue;
import io.github.mikenakis.kit.exceptions.UncheckedException;

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
