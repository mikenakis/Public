package io.github.mikenakis.bytecode.exceptions;

import io.github.mikenakis.bytecode.model.constants.ValueConstant;
import io.github.mikenakis.kit.exceptions.UncheckedException;

/**
 * "Invalid {@link ValueConstant} Tag" exception.
 *
 * @author michael.gr
 */
public final class InvalidValueConstantTagException extends UncheckedException
{
	public final int valueConstantTag;

	public InvalidValueConstantTagException( int valueConstantTag )
	{
		this.valueConstantTag = valueConstantTag;
	}
}
