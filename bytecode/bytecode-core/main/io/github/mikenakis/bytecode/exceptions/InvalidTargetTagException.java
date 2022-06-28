package io.github.mikenakis.bytecode.exceptions;

import io.github.mikenakis.kit.exceptions.UncheckedException;

import java.lang.annotation.Target;

/**
 * "Invalid {@link Target} Tag" exception.
 *
 * @author michael.gr
 */
public final class InvalidTargetTagException extends UncheckedException
{
	public final int targetTag;

	public InvalidTargetTagException( int targetTag )
	{
		this.targetTag = targetTag;
	}
}
