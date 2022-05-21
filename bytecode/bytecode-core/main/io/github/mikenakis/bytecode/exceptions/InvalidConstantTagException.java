package io.github.mikenakis.bytecode.exceptions;

import io.github.mikenakis.bytecode.model.Constant;
import io.github.mikenakis.kit.UncheckedException;

/**
 * "Invalid {@link Constant} Tag" exception.
 *
 * @author michael.gr
 */
public final class InvalidConstantTagException extends UncheckedException
{
	public final int constantTag;

	public InvalidConstantTagException( int constantTag )
	{
		this.constantTag = constantTag;
	}
}
